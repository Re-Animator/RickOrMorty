package com.reanimator.rickormorty

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.reanimator.rickormorty.api.RickAndMortyApiService
import com.reanimator.rickormorty.api.toCharacterData
import com.reanimator.rickormorty.db.CharacterData
import com.reanimator.rickormorty.db.MortyDatabase
import com.reanimator.rickormorty.db.convertIdListToString

class CharacterPagingSource(
    private val service: RickAndMortyApiService,
    private val database: MortyDatabase,
    private val idList: List<Int>?
) : PagingSource<Int, CharacterData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterData> {
        return try {
            val response = idList.convertIdListToString()?.let {
                service.getCharactersById(it).map {
                    it.toCharacterData()
                }
            }
            database.characterDao().insertAll(response!!)

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