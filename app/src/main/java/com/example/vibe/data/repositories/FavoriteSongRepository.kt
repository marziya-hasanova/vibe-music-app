package com.example.vibe.data.repositories

import com.example.vibe.data.db.FavoriteSongDao
import com.example.vibe.domain.models.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoriteSongRepository @Inject constructor(
    private val favoriteSongDao: FavoriteSongDao
){
    suspend fun getAllFavorites() = withContext(Dispatchers.IO) {
        favoriteSongDao.getAll()
    }

    suspend fun addToFavorites(song: Song) = withContext(Dispatchers.IO) {
        favoriteSongDao.insert(song)
    }

    suspend fun deleteFromFavorites (song: Song) = withContext(Dispatchers.IO) {
        favoriteSongDao.delete(song)
    }
}