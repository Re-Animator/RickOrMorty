package com.reanimator.rickormorty.ui.character

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.reanimator.rickormorty.EpisodePagingSource
import com.reanimator.rickormorty.api.RickAndMortyApiService
import com.reanimator.rickormorty.api.toLocationData
import com.reanimator.rickormorty.data.filters.CharacterFilter
import com.reanimator.rickormorty.db.CharacterData
import com.reanimator.rickormorty.db.EpisodeData
import com.reanimator.rickormorty.db.LocationData
import com.reanimator.rickormorty.db.MortyDatabase
import com.reanimator.rickormorty.db.convertStringToNonNullIntList
import com.reanimator.rickormorty.mediator.CharacterRemoteMediator
import com.reanimator.rickormorty.observer.NetworkConnectivityObserver
import com.reanimator.rickormorty.utils.Constants.Companion.NETWORK_PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val LOCATION_BASE_URL = "https://rickandmortyapi.com/api/location/"

class CharacterRepository @Inject constructor(
    private val service: RickAndMortyApiService,
    private val database: MortyDatabase,
    private val context: Context
) {
    @Inject
    lateinit var connectivityObserver: NetworkConnectivityObserver

    fun getCharacterResultStream(filter: CharacterFilter): Flow<PagingData<CharacterData>> {
        val searchQuery = filter.searchQuery
        val dbQuery = if (!searchQuery.isNullOrEmpty()) "%$searchQuery%" else searchQuery
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true,
                prefetchDistance = NETWORK_PAGE_SIZE * 5,
                initialLoadSize = NETWORK_PAGE_SIZE * 4
            ),
            remoteMediator = CharacterRemoteMediator(
                service,
                database,
                filter
            ),
            pagingSourceFactory = {
                database.characterDao().getCharacterStream(
                    searchQuery = dbQuery,
                    genderFilter = filter.gender,
                    statusFilter = filter.status
                )
            }
        ).flow
    }

    fun getCharacterEpisodesResultStream(episodeIdList: List<Int>): Flow<PagingData<EpisodeData>> {
        return Pager(
            config = PagingConfig(
                pageSize = episodeIdList.count()
            ),
            pagingSourceFactory = { EpisodePagingSource(service, database, episodeIdList, context) }
        ).flow
    }

    suspend fun getEpisodesByCharacterId(id: Int): List<Int> =
        database.characterDao().getCharacterEpisodes(id)
            .convertStringToNonNullIntList()

    suspend fun getCharacterById(id: Int): CharacterData =
        database.characterDao().getCharacterById(id)

    suspend fun getCharacterLocation(characterId: Int): LocationData? {
        val locationId = database.characterDao().getCharactersLocationId(characterId)
        return getLocationById(locationId)
    }

    suspend fun getCharacterOrigin(characterId: Int): LocationData? {
        val originId = database.characterDao().getCharactersOriginId(characterId)
        return getLocationById(originId)
    }

    private suspend fun getLocationById(locationId: Int?): LocationData? {
        val location =
            if (locationId != null) database.locationDao().getLocationById(locationId) else {
                null
            }
        return try {
            if (location == null && locationId != null) {
                val locationJson = service.getLocationFullUrl(LOCATION_BASE_URL + "$locationId")
                val locationData = locationJson.toLocationData()
                database.locationDao().insert(locationData)
                locationData
            } else {
                location
            }
        } catch (e: Exception) {
            null
        }
    }
}