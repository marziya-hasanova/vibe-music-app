package com.example.vibe

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.vibe.data.db.FavoriteSongDatabase
import com.example.vibe.presentation.ui.activities.SettingsActivity
import dagger.Provides
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Singleton

@HiltAndroidApp
class VibeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        SettingsActivity.applyTheme(this)
    }

}

