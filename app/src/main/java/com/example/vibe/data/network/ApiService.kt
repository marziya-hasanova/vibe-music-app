package com.example.vibe.data.network

import com.example.vibe.BASE_URL
import com.example.vibe.domain.models.MusicData
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {

    @GET("search")
    suspend fun getData(
        @Query("q") query: String,
        @Header("X-RapidAPI-Key") key: String,
    ): MusicData
}

object RetrofitClient {

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}