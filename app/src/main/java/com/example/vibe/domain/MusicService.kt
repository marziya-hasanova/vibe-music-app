package com.example.vibe.domain

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.service.media.MediaBrowserService
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.example.vibe.R
import com.example.vibe.domain.models.Song
import com.example.vibe.presentation.ui.activities.MainActivity
import javax.inject.Inject

class MusicService : Service() {

    private val binder = MyServiceBinder()
    private lateinit var player: ExoPlayer

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel():String {
        Log.i("LOG_TAG", "createNotificationChannel")

        val channelId = "my_service"
        val channelName = "Music Service"
        NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE).also {
            it.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(it)
        }
        return channelId
    }

    private fun displayForegroundNotification(){
        Log.i("LOG_TAG", "displayForegroundNotification")
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            } else {
                ""
            }
        val notificationIntent = Intent(this, MainActivity::class.java )
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Playing Music")
            .setContentText("vbnm")
            .setSmallIcon(R.drawable.baseline_music_note_24)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setWhen(0)
            .build()
        startForeground(1001, notification)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            } else {
                ""
            }
    }
    fun startMusic(song: Song){
        try {
            player.stop()
            player.release()
        } catch (_: UninitializedPropertyAccessException) {
        }

        player = ExoPlayer.Builder(this).build()
        val mediaItem = createMediaItem(song)

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        displayForegroundNotification()
    }

    fun stopMusic() {
        try {
            player.stop()
        } catch(_: UninitializedPropertyAccessException) {
        }
        stopForeground(STOP_FOREGROUND_DETACH)  //stopForeground(false)
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun createMediaItem(song: Song): MediaItem {
        return MediaItem.Builder()
            .setUri(song.link)
            .setMediaId(song.link) // Use a unique identifier for each song
            .setMimeType(MimeTypes.BASE_TYPE_AUDIO)
            .setMediaMetadata(MediaMetadata.Builder()
                .setTitle(song.title)
                .setArtist(song.artist)
                .setArtworkUri(Uri.parse(song.cover))
                .build())
            .build()
    }

    inner class MyServiceBinder: Binder() {
        fun getService() = this@MusicService
    }
}