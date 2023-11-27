package com.example.vibe.presentation.ui.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibe.utils.API_KEY
import com.example.vibe.domain.repositories.MusicRepository
import com.example.vibe.domain.models.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MusicViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _searchResults = MutableLiveData<List<Song>>()
    val searchResults: LiveData<List<Song>> = _searchResults

    fun getResults(query: String) {
        viewModelScope.launch {
            try {
                val response = musicRepository.getData(query, API_KEY)
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

