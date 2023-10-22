package com.reanimator.rickormorty.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<CharacterData>)

    @Query("SELECT * FROM characters WHERE id = :characterId")
    suspend fun getCharacterById(characterId: Int): CharacterData

    @Query("SELECT * FROM characters")
    fun getCharacters(): PagingSource<Int, CharacterData>

    @Query(
        "SELECT * FROM characters WHERE name LIKE " +
                "(CASE WHEN :searchQuery IS NULL THEN name ELSE :searchQuery END) " +
                "AND status LIKE" +
                "(CASE WHEN :statusFilter IS NULL THEN status ELSE :statusFilter END) " +
                "AND gender LIKE" +
                "(CASE WHEN :genderFilter IS NULL THEN gender ELSE :genderFilter END)"
    )
    fun getCharacterStream(
        searchQuery: String?,
        statusFilter: String?,
        genderFilter: String?
    ): PagingSource<Int, CharacterData>

    @Query("SELECT episode FROM characters WHERE id LIKE :characterId")
    suspend fun getCharacterEpisodes(characterId: Int): String

    @Query("DELETE FROM characters")
    suspend fun clearCharacters()

    @Query("SELECT locationId FROM characters WHERE id = :characterId")
    suspend fun getCharactersLocationId(characterId: Int): Int?

    @Query("SELECT originId FROM characters WHERE id = :characterId")
    suspend fun getCharactersOriginId(characterId: Int): Int?

    @Query("SELECT * FROM characters WHERE id IN (:characterIdList)")
    fun getCharacterListById(characterIdList: List<Int>): PagingSource<Int, CharacterData>
}

@Dao
interface CharacterRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefaultKey(characterRemoteKey: CharacterRemoteKey)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(characterRemoteKey: CharacterRemoteKey)

    @Query("UPDATE character_remote_keys SET nextKey = :nextListKey WHERE id = 1")
    suspend fun updateNextDefaultListKey(nextListKey: Int?)

    //    @Query("SELECT * FROM character_remote_keys WHERE label LIKE " +
//            "(CASE WHEN :query IS NULL THEN label ELSE :query END) " +
//            "AND status LIKE " +
//            "(CASE WHEN :status IS NULL THEN status ELSE :status END) " +
//            "AND gender LIKE " +
//            "(CASE WHEN :gender IS NULL THEN gender ELSE :gender END)")
    @Query(
        "SELECT * FROM character_remote_keys WHERE (label = :query OR label IS NULL AND :query IS NULL)" +
                " AND (status = :status OR status IS NULL AND :status IS NULL)" +
                " AND (gender = :gender OR gender IS NULL AND :gender IS NULL)"
    )
    suspend fun remoteKeyByFilterValues(
        query: String?,
        status: String?,
        gender: String?
    ): CharacterRemoteKey?

    @Query("DELETE FROM character_remote_keys WHERE label = :query")
    suspend fun deleteByQuery(query: String?)

    @Query("SELECT * FROM character_remote_keys WHERE id = 1")
    suspend fun getDefaultCharacterListKey(): CharacterRemoteKey?

    @Query("DELETE FROM character_remote_keys WHERE id != 1")
    suspend fun deleteNonDefaultKeys()
}

@Dao
interface EpisodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(episodes: List<EpisodeData>)

    @Query("SELECT * FROM episodes WHERE id LIKE :episodeId")
    suspend fun getEpisodeById(episodeId: Int): EpisodeData

    @Query(
        "SELECT * FROM episodes WHERE name LIKE " +
                "(CASE WHEN :searchQuery IS NULL THEN name ELSE :searchQuery END) " +
                "AND episode LIKE" +
                "(CASE WHEN :season IS NULL THEN episode ELSE :season END)"
    )
    fun getEpisodesStream(
        searchQuery: String?,
        season: String?
    ): PagingSource<Int, EpisodeData>


    @Query("SELECT characters FROM episodes WHERE id LIKE :episodeId")
    suspend fun getEpisodeCharacters(episodeId: Int): String

    @Query("SELECT * FROM episodes WHERE id IN (:episodeIdList)")
    fun getEpisodesListById(episodeIdList: List<Int>): PagingSource<Int, EpisodeData>

    @Query("DELETE FROM episodes")
    suspend fun clearEpisodes()
}

@Dao
interface EpisodeRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(episodeRemoteKey: EpisodeRemoteKey)

    @Query(
        "SELECT * FROM episode_remote_keys WHERE searchQuery LIKE " +
                "(CASE WHEN :searchQuery IS NULL THEN searchQuery ELSE :searchQuery END) " +
                "AND season LIKE " +
                "(CASE WHEN :season IS NULL THEN season ELSE :season END)"
    )
    suspend fun remoteEpisodeKeyByFilterValues(
        searchQuery: String?,
        season: String?
    ): EpisodeRemoteKey?

    @Query("SELECT * FROM episode_remote_keys WHERE id = 1")
    suspend fun getDefaultEpisodeListKey(): EpisodeRemoteKey?

    @Query("DELETE FROM episode_remote_keys WHERE id != 1")
    suspend fun deleteNonDefaultKeys()
}

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(locations: List<LocationData>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: LocationData)

    @Query("SELECT * FROM locations WHERE id = :locationId")
    suspend fun getLocationById(locationId: Int?): LocationData

    @Query(
        "SELECT * FROM locations WHERE name LIKE " +
                "(CASE WHEN :nameSearchQuery IS NULL THEN name ELSE :nameSearchQuery END) " +
                "AND type LIKE" +
                "(CASE WHEN :typeSearchQuery IS NULL THEN type ELSE :typeSearchQuery END)"
    )
    fun getLocationsStream(
        nameSearchQuery: String?,
        typeSearchQuery: String?
    ): PagingSource<Int, LocationData>

    @Query("SELECT residents FROM locations WHERE id LIKE :locationId")
    suspend fun getLocationCharacters(locationId: Int): String?

    @Query("DELETE FROM locations")
    suspend fun clearLocations()
}

@Dao
interface LocationRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(locationRemoteKey: LocationRemoteKey)

    @Query(
        "SELECT * FROM location_remote_keys WHERE nameSearchQuery LIKE " +
                "(CASE WHEN :nameSearchQuery IS NULL THEN nameSearchQuery ELSE :nameSearchQuery END) " +
                "AND typeSearchQuery LIKE " +
                "(CASE WHEN :typeSearchQuery IS NULL THEN typeSearchQuery ELSE :typeSearchQuery END)"
    )
    suspend fun remoteLocationsKeyByFilterValues(
        nameSearchQuery: String?,
        typeSearchQuery: String?
    ): LocationRemoteKey?

    @Query("SELECT * FROM location_remote_keys WHERE id = 1")
    suspend fun getDefaultLocationListKey(): LocationRemoteKey?

    @Query("DELETE FROM location_remote_keys WHERE id != 1")
    suspend fun deleteNonDefaultKeys()
}