package com.example.vibe

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VibeApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }

}

