package com.example.vibe

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.vibe.data.db.FavoriteSongDatabase
import com.example.vibe.presentation.ui.activities.SettingsActivity
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VibeApp : Application() {

    private lateinit var dataBase: FavoriteSongDatabase

    override fun onCreate() {
        super.onCreate()
        SettingsActivity.applyTheme(this)

        dataBase = Room.databaseBuilder(
            this,
            FavoriteSongDatabase::class.java,
            "favoriteSongs-db"
        ).build()
    }
}

val Context.app: VibeApp
    get() = applicationContext as VibeApp