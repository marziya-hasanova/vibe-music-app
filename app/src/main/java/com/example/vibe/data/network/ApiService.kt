package com.example.vibe.data.network

import com.example.vibe.domain.models.MusicData
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {
    @GET("search")
    suspend fun getData(
        @Query("q") query: String,
        @Header("X-RapidAPI-Key") key: String,
    ): MusicData
}

