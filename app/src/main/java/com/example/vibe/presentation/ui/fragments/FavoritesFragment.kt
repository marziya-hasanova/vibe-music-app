package com.example.vibe.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vibe.R
import com.example.vibe.databinding.FragmentFavoritesBinding
import com.example.vibe.domain.models.Song
import com.example.vibe.presentation.FavoritesAdapter
import com.example.vibe.presentation.interfaces.OnItemClickListener
import com.example.vibe.presentation.ui.viewModels.FavoritesViewModel
import com.example.vibe.presentation.ui.viewModels.MusicPlayerViewModel
import java.text.Normalizer


class FavoritesFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentFavoritesBinding
    private val recyclerView: RecyclerView by lazy { binding.recyclerView }
    private var adapter = FavoritesAdapter(this)
    private val favoritesViewModel by activityViewModels<FavoritesViewModel>()
    private val musicPlayerViewModel by activityViewModels<MusicPlayerViewModel>()

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
                musicPlayerViewModel.setFavSongList(it)
            } else {
                adapter.submitList(emptyList())
                Toast.makeText(
                    requireContext(),
                    "There is nothing to show :(",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_menu_search, menu)

                val searchItem = menu.findItem(R.id.search_text)
                val searchView = searchItem?.actionView as SearchView

                searchView.isSubmitButtonEnabled = true
                searchSong(searchView)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }

        }, viewLifecycleOwner)

        return binding.root
    }

    override fun onItemClick(song: Song, position: Int) {
        musicPlayerViewModel.setPlayer(song, position)
        Toast.makeText(requireContext(), "play clicked", Toast.LENGTH_SHORT).show()

    }

    override fun onFavoriteItemClick(song: Song, position: Int) {
        favoritesViewModel.toggleFavoriteStatus(song)
        adapter.notifyItemChanged(position)
    }

    fun searchSong(searchView: SearchView) {

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
            val lowercaseQuery = text?.lowercase()
            for (song in favorites) {
                val normalizedQuery = Normalizer.normalize(lowercaseQuery, Normalizer.Form.NFD)
                val normalizedTitle = Normalizer.normalize(song.title.lowercase(), Normalizer.Form.NFD)
                val normalizedArtist = Normalizer.normalize(song.artist.lowercase(), Normalizer.Form.NFD)

                if (normalizedTitle.contains(normalizedQuery)
                    || normalizedArtist.contains(normalizedQuery)
                ) {
                    filteredList.add(song)
                }
            }
            adapter.filterSongs(filteredList)
        }
    }

}