package com.example.vibe.data.repositories

import com.example.vibe.domain.models.MusicData
import retrofit2.http.Header
import retrofit2.http.Query

interface MusicRepository {
    suspend fun getData(
        query: String,
        key: String,
    ): MusicData
}