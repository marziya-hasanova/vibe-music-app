package com.example.vibe.domain

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder

class ServiceConnection: ServiceConnection {

    private var bound = false
    private lateinit var service: MusicService

    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        val myServiceBinder = binder as MusicService.MyServiceBinder
        service = myServiceBinder.getService()
        bound = true
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        bound = false
    }
}