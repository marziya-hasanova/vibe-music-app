package com.example.vibe.presentation.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibe.domain.interactors.MusicInteractor
import com.example.vibe.utils.API_KEY
import com.example.vibe.domain.models.Song
import com.example.vibe.utils.SEARCH_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MusicViewModel @Inject constructor(
    private val musicInteractor: MusicInteractor,
) : ViewModel() {

    private val _searchResults = MutableLiveData<List<Song>>()
    val searchResults: LiveData<List<Song>> = _searchResults

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    fun getResults(query: String) {
        viewModelScope.launch {
            try {
                val songs = musicInteractor.getMusicData(query, API_KEY)
                _searchResults.value = songs

            } catch (e: Exception) {
                _toastMessage.postValue(SEARCH_ERROR_MESSAGE)
            }
        }
    }

}

