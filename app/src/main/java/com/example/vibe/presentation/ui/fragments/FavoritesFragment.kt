package com.example.vibe.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.vibe.R
import com.example.vibe.databinding.FragmentFavoritesBinding
import com.example.vibe.domain.models.Song
import com.example.vibe.presentation.ui.adapters.FavoritesAdapter
import com.example.vibe.presentation.interfaces.OnItemClickListener
import com.example.vibe.presentation.ui.viewmodels.FavoritesViewModel
import com.example.vibe.presentation.ui.viewmodels.MusicPlayerViewModel
import com.example.vibe.utils.filterList
import com.example.vibe.utils.setMenuToFavoritesFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint

class FavoritesFragment : Fragment(), OnItemClickListener {


    private lateinit var binding: FragmentFavoritesBinding
    private var songList = mutableListOf<Song>()
    private val recyclerView: RecyclerView by lazy { binding.recyclerView }
    private var adapter = FavoritesAdapter(songList, this)
    private val favoritesViewModel by activityViewModels<FavoritesViewModel>()
    private val musicPlayerViewModel by activityViewModels<MusicPlayerViewModel>()

    @Inject
    lateinit var imageLoader: RequestManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized.not()) {
            binding = FragmentFavoritesBinding.inflate(layoutInflater)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )

        favoritesViewModel.getAll()

        favoritesViewModel.isFavoriteIcon.observe(viewLifecycleOwner) {
            binding.favoriteDefault.setImageResource(it)
        }

        favoritesViewModel.favorites.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                adapter.submitList(it)
            } else {
                adapter.submitList(emptyList())
                Toast.makeText(
                    requireContext(),
                    getString(R.string.there_is_nothing_to_show),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        setMenuToFavoritesFragment(requireActivity(), viewLifecycleOwner, ::searchSong)

        return binding.root
    }

    override fun onItemClick(song: Song, position: Int) {
        musicPlayerViewModel.isFavoritesListActive = true
        musicPlayerViewModel.setFavSongList(songList)
        musicPlayerViewModel.setSongToPlayer(song, position)
    }

    override fun onFavoriteItemClick(song: Song, position: Int) {
        favoritesViewModel.toggleFavoriteStatus(song)
        favoritesViewModel.favorites.observe(viewLifecycleOwner){
            musicPlayerViewModel.setFavSongList(it)
        }
        adapter.notifyItemChanged(position)
        adapter.notifyItemRemoved(position)
    }

    private fun searchSong(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.onActionViewCollapsed()
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterSongs(newText?.lowercase())
                return true
            }
        })
    }

    fun filterSongs(text: String?) {
        val filteredList = mutableListOf<Song>()
        favoritesViewModel.favorites.value?.let { favorites ->
            filterList(favorites, filteredList, text)
            adapter.filterSongs(filteredList)
        }
    }

}

