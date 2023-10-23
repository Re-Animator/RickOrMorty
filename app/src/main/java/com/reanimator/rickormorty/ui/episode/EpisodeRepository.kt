package com.reanimator.rickormorty.ui.episode

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.reanimator.rickormorty.CharacterPagingSource
import com.reanimator.rickormorty.api.RickAndMortyApiService
import com.reanimator.rickormorty.data.filters.EpisodeFilter
import com.reanimator.rickormorty.db.CharacterData
import com.reanimator.rickormorty.db.EpisodeData
import com.reanimator.rickormorty.db.MortyDatabase
import com.reanimator.rickormorty.db.convertStringToNonNullIntList
import com.reanimator.rickormorty.mediator.EpisodeRemoteMediator
import com.reanimator.rickormorty.utils.Constants.Companion.NETWORK_PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EpisodeRepository @Inject constructor(
    private val service: RickAndMortyApiService,
    private val database: MortyDatabase,
    private val context: Context
) {
    fun getEpisodeResultStream(filter: EpisodeFilter): Flow<PagingData<EpisodeData>> {
        val dbSearchQuery = if (filter.searchQuery == null) null else "%${filter.searchQuery}%"
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true,
                prefetchDistance = NETWORK_PAGE_SIZE * 2,
                initialLoadSize = NETWORK_PAGE_SIZE * 3
            ),
            remoteMediator = EpisodeRemoteMediator(
                service,
                database,
                filter
            ),
            pagingSourceFactory = {
                database.episodeDao().getEpisodesStream(
                    searchQuery = dbSearchQuery,
                    season = filter.season
                )
            }
        ).flow
    }

    fun getEpisodeCharactersResultStream(characterIdList: List<Int>): Flow<PagingData<CharacterData>> {
        return Pager(
            config = PagingConfig(
                pageSize = characterIdList.count()
            ),
            pagingSourceFactory = {
                CharacterPagingSource(
                    service = service,
                    database = database,
                    idList = characterIdList,
                    context = context
                )
            }
        ).flow
    }

    suspend fun getCharactersByEpisodeId(characterId: Int) =
        database.episodeDao().getEpisodeCharacters(characterId)
            .convertStringToNonNullIntList()

    suspend fun getEpisodeById(episodeId: Int): EpisodeData =
        database.episodeDao().getEpisodeById(episodeId)
}