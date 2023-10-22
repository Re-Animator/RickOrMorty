package com.reanimator.rickormorty.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponseCharacter(
    @Json(name = "info") override val info: Info,
    @Json(name = "results") val results: List<CharacterJson>
) : ApiResponse(info)

@JsonClass(generateAdapter = true)
data class ApiResponseEpisode(
    @Json(name = "info") override val info: Info,
    @Json(name = "results") val results: List<EpisodeJson>
) : ApiResponse(info)

@JsonClass(generateAdapter = true)
data class ApiResponseLocation(
    @Json(name = "info") override val info: Info,
    @Json(name = "results") val results: List<LocationJson>
) : ApiResponse(info)

@JsonClass(generateAdapter = true)
data class Info(
    @Json(name = "count") val count: Int,
    @Json(name = "pages") val pages: Int,
    @Json(name = "next") val nextKey: String?,
    @Json(name = "prev") val prevKey: String?
)

open class ApiResponse(
    open val info: Info
)