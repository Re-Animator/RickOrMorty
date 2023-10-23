package com.reanimator.rickormorty.di

import android.content.Context
import com.reanimator.rickormorty.api.RickAndMortyApiService
import com.reanimator.rickormorty.db.MortyDatabase
import com.reanimator.rickormorty.observer.NetworkConnectivityObserver
import com.reanimator.rickormorty.ui.character.CharacterRepository
import com.reanimator.rickormorty.ui.episode.EpisodeRepository
import com.reanimator.rickormorty.ui.location.LocationRepository
import com.reanimator.rickormorty.utils.Constants.Companion.BASE_URl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    fun logging() = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    fun okHttpClient() = OkHttpClient.Builder()
        .addInterceptor(logging())
        .build()

    @Provides
    @Singleton
    fun provideService(): RickAndMortyApiService =
        Retrofit.Builder()
            .baseUrl(BASE_URl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient())
            .build()
            .create(RickAndMortyApiService::class.java)

    @Provides
    @Singleton
    fun provideCharacterRepository(
        service: RickAndMortyApiService,
        database: MortyDatabase,
        @ApplicationContext context: Context
    ): CharacterRepository {
        return CharacterRepository(service, database, context)
    }

    @Provides
    @Singleton
    fun provideEpisodeRepository(
        service: RickAndMortyApiService,
        database: MortyDatabase,
        @ApplicationContext context: Context
    ): EpisodeRepository {
        return EpisodeRepository(service, database, context)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        service: RickAndMortyApiService,
        database: MortyDatabase,
        @ApplicationContext context: Context
    ): LocationRepository {
        return LocationRepository(service, database, context)
    }
//
//    @Provides
//    fun provideContext(application: Application): Context {
//        return application.applicationContext
//    }

    @Provides
    @Singleton
    fun provideNetworkConnectivityObserver(
        @ApplicationContext context: Context
    ): NetworkConnectivityObserver {
        return NetworkConnectivityObserver(context)
    }
}