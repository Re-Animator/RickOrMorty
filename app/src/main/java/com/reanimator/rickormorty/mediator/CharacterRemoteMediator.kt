package com.reanimator.rickormorty.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.reanimator.rickormorty.api.RickAndMortyApiService
import com.reanimator.rickormorty.api.toCharacterData
import com.reanimator.rickormorty.data.filters.CharacterFilter
import com.reanimator.rickormorty.db.CharacterData
import com.reanimator.rickormorty.db.CharacterRemoteKey
import com.reanimator.rickormorty.db.MortyDatabase
import com.reanimator.rickormorty.utils.Constants
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediator(
    private val service: RickAndMortyApiService,
    private val database: MortyDatabase,
    private val filter: CharacterFilter = CharacterFilter()
) : RemoteMediator<Int, CharacterData>() {
    override suspend fun initialize(): InitializeAction {
        return super.initialize()
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterData>
    ): MediatorResult {
        val defaultData = filter.searchQuery == null
                && filter.status == null && filter.gender == null
        val remoteKey = database.withTransaction {
            if (defaultData) {
                database.characterKeyDao().getDefaultCharacterListKey()
            } else {
                database.characterKeyDao()
                    .remoteKeyByFilterValues(filter.searchQuery, filter.status, filter.gender)
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
                database.characterKeyDao().deleteNonDefaultKeys()
            }
            val response = service.getCharactersStream(
                page = page,
                name = filter.searchQuery,
                status = filter.status,
                gender = filter.gender
            )
            val characters = response.results
            val nextPageInfo = response.info.nextKey
            val nextKey = if (nextPageInfo == null) null else page + 1
            val endOfPaginationReached = nextKey == null

            val charactersToWrite = characters.map {
                it.toCharacterData()
            }

            database.withTransaction {
                val searchQuery = filter.searchQuery
                database.characterDao().insertAll(charactersToWrite)

                val keyId = if (defaultData) Constants.DEFAULT_DATA_ID else Constants.FILTER_ID
                if (defaultData) {
                    database.characterKeyDao().insertDefaultKey(
                        CharacterRemoteKey(
                            id = keyId,
                            label = searchQuery,
                            status = filter.status,
                            gender = filter.gender,
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