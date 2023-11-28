package com.example.vibe.presentation.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vibe.R
import com.example.vibe.utils.VIEW_TYPE_ALL_FAVORITES_HEADER
import com.example.vibe.utils.VIEW_TYPE_ALL_FAVORITES_ITEM
import com.example.vibe.databinding.HeaderLayoutBinding
import com.example.vibe.databinding.SongItemBinding
import com.example.vibe.domain.models.Song
import com.example.vibe.presentation.viewholders.LibraryViewHolder
import com.example.vibe.presentation.interfaces.OnItemClickListener
import com.example.vibe.utils.MY_FAVORITES

class FavoritesAdapter(
    private var favoritesList: MutableList<Song>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<LibraryViewHolder>() {

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
                holder.header.text = MY_FAVORITES
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
        favoritesList.clear()
        favoritesList.addAll(filteredList)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(favorites: List<Song>) {
        favoritesList.clear()
        favoritesList.addAll(favorites)
        notifyDataSetChanged()
    }
}