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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vibe.R
import com.example.vibe.databinding.FragmentHomeBinding
import com.example.vibe.domain.models.Song
import com.example.vibe.presentation.adapters.MusicAdapter
import com.example.vibe.presentation.interfaces.OnItemClickListener
import com.example.vibe.presentation.ui.viewModels.FavoritesViewModel
import com.example.vibe.presentation.ui.viewModels.MusicPlayerViewModel
import com.example.vibe.presentation.ui.viewModels.MusicViewModel
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.BlurTransformation

@AndroidEntryPoint
class HomeFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private var songList = mutableListOf<Song>()
    private val recyclerView: RecyclerView by lazy { binding.recyclerView }
    private var adapter = MusicAdapter(songList, this)
    private val musicViewModel: MusicViewModel by viewModels()
    private val favoritesViewModel by activityViewModels<FavoritesViewModel>()
    private val musicPlayerViewModel by activityViewModels<MusicPlayerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized.not()) {
            binding = FragmentHomeBinding.inflate(layoutInflater)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

        Glide.with(requireContext())
            .load(R.drawable.bg)
            .transform(BlurTransformation(24, 4))
            .into(binding.fragmentBg)

        musicViewModel.searchResults.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                val updatedList = it.map { song ->
                    song.copy(isFavorite = favoritesViewModel.favorites.value?.any { it.id == song.id } == true)
                }
                adapter.updateAdapter(updatedList)
                musicPlayerViewModel.setSongList(it)
            } else {
                Toast.makeText(requireContext(), "There is nothing to show :(", Toast.LENGTH_SHORT).show()
            }
        }

        favoritesViewModel.getAll()

        favoritesViewModel.favorites.observe(viewLifecycleOwner){
            adapter.updateFavorites(it)
        }

        setMenu()

        return binding.root
    }


    override fun onItemClick(song: Song, position: Int) {
        musicPlayerViewModel.setSongToPlayer(song, position)
        Toast.makeText(requireContext(), "play clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onFavoriteItemClick(song: Song, position: Int) {
        favoritesViewModel.toggleFavoriteStatus(song)
        adapter.notifyItemChanged(position)
    }

    fun setMenu(){
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_menu_search, menu)

                val searchItem = menu.findItem(R.id.search_text)
                val searchView = searchItem?.actionView as SearchView

                searchView.isSubmitButtonEnabled = true
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        musicViewModel.getResults(query.toString())
                        searchView.clearFocus()
                        searchView.onActionViewCollapsed()
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }

        }, viewLifecycleOwner)
    }
}
