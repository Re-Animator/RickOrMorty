package com.reanimator.rickormorty.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.reanimator.rickormorty.api.RickAndMortyApiService
import com.reanimator.rickormorty.api.toEpisodeData
import com.reanimator.rickormorty.data.filters.EpisodeFilter
import com.reanimator.rickormorty.db.EpisodeData
import com.reanimator.rickormorty.db.EpisodeRemoteKey
import com.reanimator.rickormorty.db.MortyDatabase
import com.reanimator.rickormorty.utils.Constants
import com.reanimator.rickormorty.utils.Constants.Companion.DEFAULT_DATA_ID
import com.reanimator.rickormorty.utils.Constants.Companion.FILTER_ID
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class EpisodeRemoteMediator(
    private val service: RickAndMortyApiService,
    private val database: MortyDatabase,
    private val filter: EpisodeFilter = EpisodeFilter(),
    private val idList: List<Int>? = null
) : RemoteMediator<Int, EpisodeData>() {
    override suspend fun initialize(): InitializeAction {
        return super.initialize()
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EpisodeData>
    ): MediatorResult {
        val defaultData = filter.searchQuery == null && filter.season == null
        val remoteKey = database.withTransaction {
            if (defaultData) {
                database.episodeKeyDao().getDefaultEpisodeListKey()
            } else {
                database.episodeKeyDao()
                    .remoteEpisodeKeyByFilterValues(filter.searchQuery, filter.season)
            }
        }

        val page = when (loadType) {
            LoadType.REFRESH -> {
                remoteKey?.nextKey ?: Constants.START_PAGINATION_PAGE
            }

            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            LoadType.APPEND -> {
                if (remoteKey?.nextKey == null) return MediatorResult.Success(endOfPaginationReached = true)
                remoteKey.nextKey
            }
        }
        try {
            if (loadType == LoadType.REFRESH) {
                database.episodeKeyDao().deleteNonDefaultKeys()
            }
            val response = service.getEpisodeStream(
                page = page,
                name = filter.searchQuery,
                episode = filter.season
            )

            val episodes = response.results
            val nextPageInfo = response.info.nextKey
            val nextKey = if (nextPageInfo == null) null else page + 1
            val endOfPaginationReached = nextKey == null

            val episodesToWrite = episodes.map {
                it.toEpisodeData()
            }

            database.withTransaction {
                database.episodeDao().insertAll(episodesToWrite)
                val keyId = if (defaultData) DEFAULT_DATA_ID else FILTER_ID
                database.episodeKeyDao().insert(
                    EpisodeRemoteKey(
                        id = keyId,
                        searchQuery = filter.searchQuery,
                        season = filter.season,
                        nextKey = nextKey
                    )
                )
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }
}