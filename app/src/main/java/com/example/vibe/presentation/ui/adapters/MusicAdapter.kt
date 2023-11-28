package com.example.vibe.presentation.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.vibe.R
import com.example.vibe.domain.models.Song
import com.example.vibe.databinding.SongItemBinding
import com.example.vibe.presentation.viewholders.SongViewHolder
import com.example.vibe.presentation.interfaces.OnItemClickListener
import javax.inject.Inject

class MusicAdapter (
    private var songsList: MutableList<Song>,
    private val listener: OnItemClickListener,
) :RecyclerView.Adapter<SongViewHolder>() {

    private lateinit var binding: SongItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        binding = SongItemBinding.bind(view)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val songItem = songsList[position]
        holder.title.text = songItem.title
        holder.artist.text = songItem.artist

        Glide.with(holder.itemView)
            .load(songItem.cover)
            .placeholder(R.drawable.bg)
            .into(holder.cover)
        holder.bind(songItem, position, listener)

    }

    override fun getItemCount(): Int = songsList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(searchResults: List<Song>) {
        songsList.clear()
        songsList.addAll(searchResults)
        notifyDataSetChanged()
    }

    @Keep
    @SuppressLint("NotifyDataSetChanged")
    fun updateFavorites(newFavorites: List<Song>) {
        songsList.forEach { song ->
            song.isFavorite = newFavorites.any { favorite -> favorite.id == song.id }
        }
        notifyDataSetChanged()
    }
}