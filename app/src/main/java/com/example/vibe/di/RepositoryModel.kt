package com.example.vibe.di

import com.example.vibe.data.network.ApiService
import com.example.vibe.domain.repositories.MusicRepository
import com.example.vibe.domain.repositories.MusicRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    fun provideMusicRepository(apiService: ApiService): MusicRepository =
        MusicRepositoryImpl(apiService)
}
