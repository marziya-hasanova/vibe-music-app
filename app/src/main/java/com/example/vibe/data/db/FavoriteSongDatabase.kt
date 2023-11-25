package com.example.vibe.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.vibe.domain.models.Song


@Database(
    entities = [Song::class],
    version = 4
)
abstract class FavoriteSongDatabase : RoomDatabase(){
    abstract fun favoriteSongDao(): FavoriteSongDao
}
