package com.example.vibe.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.vibe.R
import com.example.vibe.databinding.FragmentHomeBinding
import com.example.vibe.domain.models.Song
import com.example.vibe.presentation.ui.adapters.MusicAdapter
import com.example.vibe.presentation.interfaces.OnItemClickListener
import com.example.vibe.presentation.ui.viewModels.FavoritesViewModel
import com.example.vibe.presentation.ui.viewModels.MusicPlayerViewModel
import com.example.vibe.presentation.ui.viewModels.MusicViewModel
import com.example.vibe.utils.setMenuHomeFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.BlurTransformation
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private var songList = mutableListOf<Song>()
    private val recyclerView: RecyclerView by lazy { binding.recyclerView }
    private var adapter = MusicAdapter(songList, this)
    private val musicViewModel: MusicViewModel by activityViewModels()
    private val favoritesViewModel by activityViewModels<FavoritesViewModel>()
    private val musicPlayerViewModel by activityViewModels<MusicPlayerViewModel>()
    @Inject
    lateinit var imageLoader: RequestManager

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

       imageLoader.load(R.drawable.bg)
            .transform(BlurTransformation(24, 4))
            .into(binding.fragmentBg)

        musicViewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        musicViewModel.searchResults.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                val updatedList = it.map { song ->
                    song.copy(isFavorite = favoritesViewModel.favorites.value?.any { it.id == song.id } == true)
                }
                adapter.updateAdapter(updatedList)
            } else {
                Toast.makeText(requireContext(), "There is nothing to show :(", Toast.LENGTH_SHORT).show()
            }
        }

        favoritesViewModel.getAll()

        favoritesViewModel.favorites.observe(viewLifecycleOwner){
            adapter.updateFavorites(it)
        }

        setMenuHomeFragment(requireActivity(), musicViewModel, viewLifecycleOwner)

        return binding.root
    }


    override fun onItemClick(song: Song, position: Int) {
        musicPlayerViewModel.isFavoritesListActive = false
        musicPlayerViewModel.setSongList(songList)
        musicPlayerViewModel.setSongToPlayer(song, position)
    }

    override fun onFavoriteItemClick(song: Song, position: Int) {
        favoritesViewModel.toggleFavoriteStatus(song)
        favoritesViewModel.favorites.observe(viewLifecycleOwner){
            musicPlayerViewModel.setFavSongList(it)
        }
        adapter.notifyItemChanged(position)
    }

}
