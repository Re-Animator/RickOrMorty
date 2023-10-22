package com.reanimator.rickormorty.di

import android.content.Context
import androidx.room.Room
import com.reanimator.rickormorty.db.CharacterDao
import com.reanimator.rickormorty.db.MortyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun provideCharacterDao(database: MortyDatabase): CharacterDao {
        return database.characterDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): MortyDatabase {
        return Room.databaseBuilder(
            appContext,
            MortyDatabase::class.java,
            "MortyDatabase.db"
        ).build()
    }
}