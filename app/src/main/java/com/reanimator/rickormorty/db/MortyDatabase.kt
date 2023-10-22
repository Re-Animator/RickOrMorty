package com.reanimator.rickormorty.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CharacterData::class, CharacterRemoteKey::class,
        EpisodeData::class, EpisodeRemoteKey::class,
        LocationData::class, LocationRemoteKey::class],
    version = 1,
    exportSchema = false
)

abstract class MortyDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun characterKeyDao(): CharacterRemoteKeyDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun episodeKeyDao(): EpisodeRemoteKeyDao
    abstract fun locationDao(): LocationDao
    abstract fun locationKeyDao(): LocationRemoteKeyDao

    companion object {
        @Volatile
        private var INSTANCE: MortyDatabase? = null

        fun getInstance(context: Context): MortyDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                MortyDatabase::class.java,
                "MortyDatabase.db"
            ).build()
    }
}