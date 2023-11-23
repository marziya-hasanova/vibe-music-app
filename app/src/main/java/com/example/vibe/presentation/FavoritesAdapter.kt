package com.example.vibe.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vibe.R
import com.example.vibe.databinding.HeaderLayoutBinding
import com.example.vibe.databinding.SongItemBinding
import com.example.vibe.domain.models.Song
import com.example.vibe.presentation.interfaces.OnItemClickListener

class FavoritesAdapter(
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<LibraryViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ALL_FAVORITES_HEADER = 0
        private const val VIEW_TYPE_ALL_FAVORITES_ITEM = 1
    }

    private var favoritesList: List<Song> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        val binding = SongItemBinding.bind(view)

        val headerView = LayoutInflater.from(parent.context).inflate(R.layout.header_layout, parent, false)
        val headerBinding = HeaderLayoutBinding.bind(headerView)

        return when (viewType) {
            VIEW_TYPE_ALL_FAVORITES_HEADER -> {
                LibraryViewHolder.FavoritesHeader(headerBinding)
            }
            else -> {
                LibraryViewHolder.Favorites(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        when (holder) {
            is LibraryViewHolder.Favorites -> {
                // Adjust the index to account for the header
                val adjustedPosition = position - 1
                val songItem = favoritesList[adjustedPosition]

                holder.title.text = songItem.title
                holder.artist.text = songItem.artist
                Glide.with(holder.itemView)
                    .load(songItem.cover)
                    .placeholder(R.drawable.bg)
                    .into(holder.cover)
                holder.bind(songItem, adjustedPosition, listener)
            }
            is LibraryViewHolder.FavoritesHeader -> {
                holder.header.text = "My Favorites"
            }
        }
    }

    override fun getItemCount(): Int = favoritesList.size + 1

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_ALL_FAVORITES_HEADER
            else -> VIEW_TYPE_ALL_FAVORITES_ITEM
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterSongs(filteredList: List<Song>) {
        favoritesList = filteredList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(favorites: List<Song>) {
        favoritesList = favorites
        notifyDataSetChanged()
    }
}