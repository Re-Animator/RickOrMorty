package com.reanimator.rickormorty

import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.reanimator.rickormorty.api.RickAndMortyApiService
import com.reanimator.rickormorty.api.toEpisodeData
import com.reanimator.rickormorty.db.EpisodeData
import com.reanimator.rickormorty.db.MortyDatabase
import com.reanimator.rickormorty.db.convertIdListToString
import com.reanimator.rickormorty.observer.NetworkConnectivityObserver

class EpisodePagingSource(
    private val service: RickAndMortyApiService,
    private val database: MortyDatabase,
    private val idList: List<Int>,
    private val context: Context
) : PagingSource<Int, EpisodeData>() {
    private val connectivityObserver = NetworkConnectivityObserver(context)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EpisodeData> {
        Log.d("Episodes Paging", connectivityObserver.isNetworkAvailable().toString())
        return try {
            val response = if(connectivityObserver.isNetworkAvailable()) {
                val items = idList.convertIdListToString()?.let {
                    service.getEpisodesById(it).map { episode ->
                        episode.toEpisodeData()
                    }
                }
                database.episodeDao().insertAll(items!!)
                items
            } else {
                database.episodeDao().getEpisodesListById(idList)
            }

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