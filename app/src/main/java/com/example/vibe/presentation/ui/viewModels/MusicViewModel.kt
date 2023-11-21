package com.example.vibe.presentation.ui.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.vibe.R
import com.example.vibe.data.db.FavoriteSongDatabase
import com.example.vibe.data.network.RetrofitClient
import com.example.vibe.data.repositories.FavoriteSongRepository
import com.example.vibe.domain.models.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val API_KEY = "dce979c3b4msh91307c952a44c51p176f4djsn30811deb2cad"

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = RetrofitClient.apiService

    private val _searchResults = MutableLiveData<List<Song>>()
    val searchResults: LiveData<List<Song>> = _searchResults


    fun getResults(query: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getData(query, API_KEY)
                val data = response.data

                val songList = data.map {
                    Song(
                        title = it.title,
                        artist = it.artist.name,
                        cover = it.album.cover_medium,
                        link = it.preview,
                        duration = it.duration,
                        id = it.id
                    )
                }

                _searchResults.postValue(songList)
            } catch (e: Exception) {
                Log.e("responseTag", "Error: ${e.message}")
            }
        }
    }

}

