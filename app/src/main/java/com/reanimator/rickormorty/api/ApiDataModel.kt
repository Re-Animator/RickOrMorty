package com.reanimator.rickormorty.api

import com.reanimator.rickormorty.db.CharacterData
import com.reanimator.rickormorty.db.EpisodeData
import com.reanimator.rickormorty.db.LocationData
import com.reanimator.rickormorty.db.convertStringListToString
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CharacterJson(
    val created: String,
    val episode: List<String>,
    val gender: String,
    override val id: Int,
    val image: String,
    val location: CharacterLocation,
    val name: String,
    val origin: CharacterOrigin,
    val species: String,
    val status: String,
    val type: String,
    val url: String
) : ApiData(id)

fun CharacterJson.toCharacterData(): CharacterData {
    val originId = if(origin.url.isNullOrEmpty()) null
                            else origin.url.split("location/").last().toInt()
    val locationId = if(location.url.isNullOrEmpty()) null
                            else location.url.split("location/").last().toInt()
    return CharacterData(
        id = id,
        name = name,
        status = status,
        species = species,
        type = type,
        gender = gender,
        originId = originId,
        locationId = locationId,
        image = image,
        episode = episode.convertStringListToString()!!,
        url = url,
        created = created
    )
}


@JsonClass(generateAdapter = true)
data class EpisodeJson(
    override val id: Int,
    val name: String,
    val air_date: String,
    val episode: String,
    val characters: List<String>,
    val url: String,
    val created: String
) : ApiData(id)

fun EpisodeJson.toEpisodeData(): EpisodeData =
    EpisodeData(
        id = id,
        name = name,
        air_date = air_date,
        episode = episode,
        characters = characters.convertStringListToString()!!,
        url = url,
        created = created
    )

@JsonClass(generateAdapter = true)
data class LocationJson(
    override val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residents: List<String>,
    val url: String,
    val created: String
) : ApiData(id)

fun LocationJson.toLocationData(): LocationData =
    LocationData(
        id = id,
        name = name,
        type = type,
        dimension = dimension,
        residents = residents.convertStringListToString(),
        url = url,
        created = created
    )

@JsonClass(generateAdapter = true)
data class CharacterLocation(
    val name: String,
    val url: String
)

@JsonClass(generateAdapter = true)
data class CharacterOrigin(
    val name: String,
    val url: String
)

open class ApiData(
    open val id: Int
) {
    fun getId(): String = id.toString()
}