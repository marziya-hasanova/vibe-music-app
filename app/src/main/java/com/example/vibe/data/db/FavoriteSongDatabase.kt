package com.example.vibe.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.vibe.domain.models.Song

@Database(
    entities = [Song::class],
    version = 4
)
abstract class FavoriteSongDatabase : RoomDatabase(){

    abstract fun favoriteSongDao(): FavoriteSongDao

    companion object {
        @Volatile
        private var INSTANCE: FavoriteSongDatabase? = null

        fun getDatabase(context: Context): FavoriteSongDatabase {

            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteSongDatabase::class.java,
                    "note_database"
                ).fallbackToDestructiveMigration().build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }

}
