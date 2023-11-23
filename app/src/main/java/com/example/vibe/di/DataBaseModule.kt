package com.example.vibe.di

import android.app.Application
import androidx.room.Room
import com.example.vibe.data.db.FavoriteSongDao
import com.example.vibe.data.db.FavoriteSongDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFavoriteSongDatabase(app: Application): FavoriteSongDatabase =
        Room.databaseBuilder(app, FavoriteSongDatabase::class.java, "favoriteSongs-db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideFavoriteSongDao(database: FavoriteSongDatabase): FavoriteSongDao =
        database.favoriteSongDao()
}
