package com.reanimator.rickormorty.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.reanimator.rickormorty.api.RickAndMortyApiService
import com.reanimator.rickormorty.api.toLocationData
import com.reanimator.rickormorty.data.filters.LocationFilter
import com.reanimator.rickormorty.db.LocationData
import com.reanimator.rickormorty.db.LocationRemoteKey
import com.reanimator.rickormorty.db.MortyDatabase
import com.reanimator.rickormorty.utils.Constants
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class LocationRemoteMediator(
    private val service: RickAndMortyApiService,
    private val database: MortyDatabase,
    private val filter: LocationFilter = LocationFilter()
) : RemoteMediator<Int, LocationData>() {
    override suspend fun initialize(): InitializeAction {
        return super.initialize()
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LocationData>
    ): MediatorResult {
        val defaultData = filter.searchQuery == null && filter.typeSearchQuery == null
        val remoteKey = database.withTransaction {
            if (defaultData) {
                database.locationKeyDao().getDefaultLocationListKey()
            } else {
                database.locationKeyDao()
                    .remoteLocationsKeyByFilterValues(filter.searchQuery, filter.typeSearchQuery)
            }
        }
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val refreshPage = remoteKey?.nextKey ?: Constants.START_PAGINATION_PAGE
                refreshPage
            }

            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            LoadType.APPEND -> {
                if (remoteKey?.nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                remoteKey.nextKey
            }
        }
        try {
            if (loadType == LoadType.REFRESH) {
                database.locationKeyDao().deleteNonDefaultKeys()
            }
            val response = service.getLocationStream(
                page = page,
                name = filter.searchQuery,
                type = filter.typeSearchQuery
            )
            val locations = response.results
            val nextPageInfo = response.info.nextKey
            val nextKey = if (nextPageInfo == null) null else page + 1
            val endOfPaginationReached = nextKey == null
            val locationsToWrite = locations.map {
                it.toLocationData()
            }

            database.withTransaction {
                database.locationDao().insertAll(locationsToWrite)
                if (defaultData) {
                    database.locationKeyDao().insert(
                        LocationRemoteKey(
                            id = Constants.DEFAULT_DATA_ID,
                            nameSearchQuery = filter.searchQuery,
                            typeSearchQuery = filter.typeSearchQuery,
                            nextKey = nextKey
                        )
                    )
                } else {
                    database.locationKeyDao().insert(
                        LocationRemoteKey(
                            id = Constants.FILTER_ID,
                            nameSearchQuery = filter.searchQuery,
                            typeSearchQuery = filter.typeSearchQuery,
                            nextKey = nextKey
                        )
                    )
                }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }
}