package com.reanimator.rickormorty.api

import com.reanimator.rickormorty.db.EpisodeData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface RickAndMortyApiService {
    @GET("{entity}/?")
    suspend fun getEntityData(
        @Path("entity") entity: String,
        @Query("page") page: Int
    ): ApiResponse

    @GET("/episode/{episodes}")
    suspend fun getEpisodes(
        @Path("episodes") episodes: String
    ): List<EpisodeData>

    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: String
    ): ApiResponse

    @GET("character")
    suspend fun getCharactersStream(
        @Query("page") page: Int? = null,
        @Query("name") name: String? = null,
        @Query("status") status: String? = null,
        @Query("gender") gender: String? = null
    ): ApiResponseCharacter

    @GET("location")
    suspend fun getLocationStream(
        @Query("page") page: Int? = null,
        @Query("name") name: String? = null,
        @Query("type") type: String? = null
    ): ApiResponseLocation

    @GET("episode")
    suspend fun getEpisodeStream(
        @Query("page") page: Int? = null,
        @Query("name") name: String? = null,
        @Query("episode") episode: String? = null
    ): ApiResponseEpisode


    @GET("/api/episode/{episodes}")
    suspend fun getEpisodesById(
        @Path("episodes") episodes: String
    ): List<EpisodeJson>

    @GET("/api/character/{characters}")
    suspend fun getCharactersById(
        @Path("characters") characters: String
    ): List<CharacterJson>

    @GET
    suspend fun getLocationFullUrl(
        @Url url: String
    ): LocationJson
}