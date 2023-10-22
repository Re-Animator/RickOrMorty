package com.reanimator.rickormorty.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterData(
    @PrimaryKey
    override val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val originId: Int?,
    val locationId: Int?,
    val image: String,
    val episode: String,
    val url: String,
    val created: String
) : Data(id)

@Entity(tableName = "character_remote_keys")
data class CharacterRemoteKey(
    @PrimaryKey(autoGenerate = true)
    override val id: Int,
    val label: String?,
    val gender: String?,
    val status: String?,
    val nextKey: Int?
) : RemoteKey(id)

@Entity(tableName = "episodes")
data class EpisodeData(
    @PrimaryKey
    override val id: Int,
    val name: String,
    val air_date: String,
    val episode: String,
    val characters: String,
    val url: String,
    val created: String
) : Data(id)

@Entity(tableName = "episode_remote_keys")
data class EpisodeRemoteKey(
    @PrimaryKey(autoGenerate = true)
    override val id: Int = 1,
    val searchQuery: String?,
    val season: String?,
    val nextKey: Int?
) : RemoteKey(id)

@Entity(tableName = "locations")
data class LocationData(
    @PrimaryKey
    override val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residents: String?,
    val url: String,
    val created: String
) : Data(id)

@Entity(tableName = "location_remote_keys")
data class LocationRemoteKey(
    @PrimaryKey(autoGenerate = true)
    override val id: Int = 1,
    val nameSearchQuery: String?,
    val typeSearchQuery: String?,
    val nextKey: Int?
) : RemoteKey(id)

open class Data(
    open val id: Int
)

open class RemoteKey(
    open val id: Int
)