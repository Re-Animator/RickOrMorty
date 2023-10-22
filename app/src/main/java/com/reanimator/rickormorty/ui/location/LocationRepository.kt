package com.reanimator.rickormorty.ui.location

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.reanimator.rickormorty.CharacterPagingSource
import com.reanimator.rickormorty.api.RickAndMortyApiService
import com.reanimator.rickormorty.data.filters.LocationFilter
import com.reanimator.rickormorty.db.CharacterData
import com.reanimator.rickormorty.db.LocationData
import com.reanimator.rickormorty.db.MortyDatabase
import com.reanimator.rickormorty.db.convertStringToNonNullIntList
import com.reanimator.rickormorty.mediator.LocationRemoteMediator
import com.reanimator.rickormorty.utils.Constants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val service: RickAndMortyApiService,
    private val database: MortyDatabase
) {
    fun getLocationResultStream(filter: LocationFilter): Flow<PagingData<LocationData>> {
        val nameSearchQuery = filter.searchQuery
        val typeSearchQuery = filter.typeSearchQuery
        val nameDbQuery =
            if (!nameSearchQuery.isNullOrEmpty()) "%$nameSearchQuery%" else nameSearchQuery
        val typeDbQuery =
            if (!typeSearchQuery.isNullOrEmpty()) "%$typeSearchQuery%" else typeSearchQuery
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = Constants.NETWORK_PAGE_SIZE,
                enablePlaceholders = true,
                prefetchDistance = Constants.NETWORK_PAGE_SIZE,
                initialLoadSize = Constants.NETWORK_PAGE_SIZE * 3
            ),
            remoteMediator = LocationRemoteMediator(
                service,
                database,
                filter
            ),
            pagingSourceFactory = {
                database.locationDao().getLocationsStream(
                    nameSearchQuery = nameDbQuery,
                    typeSearchQuery = typeDbQuery
                )
            }
        ).flow
    }

    fun getLocationCharactersResultStream(characterIdList: List<Int>?): Flow<PagingData<CharacterData>> {
        return Pager(
            config = PagingConfig(
                pageSize = characterIdList?.count() ?: 0
            ),
            pagingSourceFactory = { CharacterPagingSource(service, database, characterIdList) }
        ).flow
    }

    suspend fun getCharactersByEpisodeId(locationId: Int) =
        database.locationDao().getLocationCharacters(locationId)
            ?.convertStringToNonNullIntList()

    suspend fun getLocationById(locationId: Int): LocationData =
        database.locationDao().getLocationById(locationId)
}