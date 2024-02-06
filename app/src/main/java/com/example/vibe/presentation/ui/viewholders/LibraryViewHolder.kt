package com.example.vibe.presentation.viewholders

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.vibe.R
import com.example.vibe.databinding.HeaderLayoutBinding
import com.example.vibe.databinding.SongItemBinding
import com.example.vibe.domain.models.Song
import com.example.vibe.presentation.interfaces.OnItemClickListener

sealed class LibraryViewHolder (binding: ViewBinding): RecyclerView.ViewHolder(binding.root){

    class FavoritesHeader(
        binding: HeaderLayoutBinding
    ) : LibraryViewHolder(binding){

        val header = binding.header
    }

    class Favorites(
        binding: SongItemBinding
    ): LibraryViewHolder(binding)  {

        val title = binding.songTitle
        val artist = binding.artistTitle
        val cover = binding.coverView
        private val isFavorite = binding.favoriteDefault

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(song: Song, position: Int, listener: OnItemClickListener){
            isFavorite.setImageResource(R.drawable.ic_favorite_filled_white)
            itemView.setOnClickListener { listener.onItemClick(song, position) }
            isFavorite.setOnClickListener { listener.onFavoriteItemClick(song, position)}
        }
    }
}
