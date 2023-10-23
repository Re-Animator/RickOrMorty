package com.reanimator.rickormorty

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.reanimator.rickormorty.api.RickAndMortyApiService
import com.reanimator.rickormorty.api.toCharacterData
import com.reanimator.rickormorty.db.CharacterData
import com.reanimator.rickormorty.db.MortyDatabase
import com.reanimator.rickormorty.db.convertIdListToString
import com.reanimator.rickormorty.observer.NetworkConnectivityObserver

class CharacterPagingSource(
    private val service: RickAndMortyApiService,
    private val database: MortyDatabase,
    private val idList: List<Int>?,
    private val context: Context
) : PagingSource<Int, CharacterData>() {
    private val connectivityObserver = NetworkConnectivityObserver(context)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterData> {
        return try {
            val response = if(connectivityObserver.isNetworkAvailable()) {
                    val items = idList.convertIdListToString()?.let {
                        service.getCharactersById(it).map { character ->
                            character.toCharacterData()
                        }
                    }
                    database.characterDao().insertAll(items!!)
                    items
                } else {
                    database.characterDao().getCharacterListById(idList!!)
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

    override fun getRefreshKey(state: PagingState<Int, CharacterData>): Int? {
        return 0
    }
}