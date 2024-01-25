package com.example.vibe.di

import com.example.vibe.data.repositories.MusicRepository
import com.example.vibe.data.repositories.MusicRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideMusicRepository(
        musicRepositoryImpl: MusicRepositoryImpl
    ):  MusicRepository

}
