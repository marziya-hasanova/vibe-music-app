package com.example.vibe.presentation.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibe.data.repositories.FavoriteSongRepository
import com.example.vibe.domain.models.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: FavoriteSongRepository
) : ViewModel() {

    private val _favorites = MutableLiveData<List<Song>>()
    val favorites: LiveData<List<Song>> = _favorites

    private val _isFavoriteIcon = MutableLiveData<Int>()
    val isFavoriteIcon: LiveData<Int> = _isFavoriteIcon

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite = _isFavorite

    fun getAll() {
        viewModelScope.launch {
            val list = repository.getAllFavorites()
            list.collect {
                _favorites.value = it
            }
        }
    }

     fun addToFavorites(song: Song) = viewModelScope.launch {
        repository.addToFavorites(song)
         refreshFavorites()
    }

     private fun removeFromFavorites(song: Song) = viewModelScope.launch {
         repository.deleteFromFavorites(song)
         _isFavorite.value = false
         refreshFavorites()
    }

    private fun refreshFavorites() = viewModelScope.launch {
        repository.getAllFavorites().collect { favoritesList ->
            _favorites.postValue(favoritesList)
        }
    }


    fun toggleFavoriteStatus(song: Song) = viewModelScope.launch {
        val isFavorite = _favorites.value?.any { it.id == song.id } == true
        if (isFavorite) {
            removeFromFavorites(song)
        } else {
            addToFavorites(song)
        }
    }

}


