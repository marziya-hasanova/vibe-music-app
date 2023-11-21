package com.example.vibe

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.vibe.data.db.FavoriteSongDatabase

class VibeApp : Application() {

    private lateinit var dataBase: FavoriteSongDatabase

    override fun onCreate() {
        super.onCreate()
        dataBase = Room.databaseBuilder(
            this,
            FavoriteSongDatabase::class.java,
            "favoriteSongs-db"
        ).build()
    }
}

val Context.app: VibeApp
    get() = applicationContext as VibeApp