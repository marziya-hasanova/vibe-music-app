package com.example.vibe.domain.repositories

import com.example.vibe.data.network.ApiService
import com.example.vibe.domain.models.MusicData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : MusicRepository {
    override suspend fun getData(query: String, key: String): MusicData =
        withContext(Dispatchers.IO) {
            apiService.getData(query, key)
        }
}

