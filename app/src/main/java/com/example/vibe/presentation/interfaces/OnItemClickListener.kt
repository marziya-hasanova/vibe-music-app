package com.example.vibe.presentation.interfaces

import com.example.vibe.domain.models.Song

interface OnItemClickListener {
    fun onItemClick(song: Song, position: Int)
    fun onFavoriteItemClick(song: Song, position: Int)
}