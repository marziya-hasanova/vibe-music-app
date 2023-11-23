package com.example.vibe.presentation.viewHolders

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.example.vibe.R
import com.example.vibe.domain.models.Song
import com.example.vibe.databinding.SongItemBinding
import com.example.vibe.presentation.interfaces.OnItemClickListener

class SongViewHolder(
    val binding: SongItemBinding
): RecyclerView.ViewHolder(binding.root)  {

    val title = binding.songTitle
    val artist = binding.artistTitle
    val cover = binding.coverView
    val isFavorite = binding.favoriteDefault

    @SuppressLint("UseCompatLoadingForDrawables")
    fun bind(song: Song, position: Int, listener: OnItemClickListener){
        isFavorite.setImageResource(
            if (song.isFavorite) R.drawable.ic_favorite_filled_white
            else R.drawable.ic_favorite_border
        )
        itemView.setOnClickListener { listener.onItemClick(song, position) }
        isFavorite.setOnClickListener { listener.onFavoriteItemClick(song, position)
        }
    }
}
