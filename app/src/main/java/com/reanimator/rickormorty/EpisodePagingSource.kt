package com.reanimator.rickormorty

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.reanimator.rickormorty.api.RickAndMortyApiService
import com.reanimator.rickormorty.api.toEpisodeData
import com.reanimator.rickormorty.db.EpisodeData
import com.reanimator.rickormorty.db.MortyDatabase
import com.reanimator.rickormorty.db.convertIdListToString

class EpisodePagingSource(
    private val service: RickAndMortyApiService,
    private val database: MortyDatabase,
    private val idList: List<Int>
) : PagingSource<Int, EpisodeData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EpisodeData> {
        return try {
            val response = idList.convertIdListToString()?.let {
                service.getEpisodesById(it).map {
                    it.toEpisodeData()
                }
            }
            database.episodeDao().insertAll(response!!)

            LoadResult.Page(
                data = response,
                prevKey = null,
                nextKey = null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, EpisodeData>): Int? {
        return 1
    }
}