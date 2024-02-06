package com.example.vibe.presentation.ui.fragments

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.RequestManager
import com.example.vibe.R
import com.example.vibe.databinding.FragmentNowPlayingBinding
import com.example.vibe.utils.ShuffleMode
import com.example.vibe.presentation.ui.viewmodels.MusicPlayerViewModel
import com.example.vibe.utils.NOTHING_ON_PLAY
import com.example.vibe.utils.formatDuration
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.ColorFilterTransformation
import javax.inject.Inject

@AndroidEntryPoint
class NowPlayingFragment : Fragment() {

    private lateinit var binding: FragmentNowPlayingBinding
    private val musicPlayerViewModel by activityViewModels<MusicPlayerViewModel>()
    private lateinit var seekBar: SeekBar
    private lateinit var progressDuration: TextView
    private lateinit var progressPlaying: TextView

    @Inject
    lateinit var imageLoader: RequestManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized.not()) {
            binding = FragmentNowPlayingBinding.inflate(layoutInflater)
        }

        val cover = binding.albumArt
        val playButton = binding.circlePlayButton
        val previousButton = binding.skipPrevious
        val nextButton = binding.skipNext
        val shuffleButton = binding.shuffle
        val repeatButton = binding.repeatButton
        val blurImage = binding.blurImageView

        binding.songTitleView.isSelected = true

        imageLoader.load(R.drawable.bg)
            .transform(
                BlurTransformation(24, 4),
            )
            .into(blurImage)

        imageLoader.load(R.drawable.bg)
            .circleCrop()
            .into(cover)

        musicPlayerViewModel.coverUrl.observe(viewLifecycleOwner) {
            imageLoader.load(it)
                .placeholder(R.drawable.bg)
                .circleCrop()
                .into(cover)

            imageLoader.load(it)
                .placeholder(R.drawable.bg)
                .transform(
                    BlurTransformation(24, 4),
                    ColorFilterTransformation(R.color.black)
                )
                .into(blurImage)

        }

        musicPlayerViewModel.title.observe(viewLifecycleOwner) {
            musicPlayerViewModel.artist.observe(viewLifecycleOwner) {
                if (musicPlayerViewModel.formattedTitle.isNotEmpty()) {
                    binding.songTitleView.text = musicPlayerViewModel.formattedTitle
                }
            }
        }

        musicPlayerViewModel.shuffleMode.observe(viewLifecycleOwner){
            if(it == ShuffleMode.SHUFFLE_ON){
                musicPlayerViewModel.setShuffledList()
            }
        }

        musicPlayerViewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            if (isPlaying) {
                playButton.setImageResource(R.drawable.baseline_pause_circle_white)
            } else {
                playButton.setImageResource(R.drawable.baseline_play_circle_white)
            }
        }


        playButton.setOnClickListener {
            if (musicPlayerViewModel.isPlaying.value == true && musicPlayerViewModel.isPlayerInitialized) {
                musicPlayerViewModel.pause()
            } else if (musicPlayerViewModel.isPlaying.value == false && musicPlayerViewModel.isPlayerInitialized) {
                musicPlayerViewModel.play()
            } else {
                Toast.makeText(requireContext(), NOTHING_ON_PLAY, Toast.LENGTH_SHORT).show()
            }
        }

        previousButton.setOnClickListener {
            if (musicPlayerViewModel.isPlayerInitialized) {
                musicPlayerViewModel.playPrevious()
            }
        }

        nextButton.setOnClickListener {
            if (musicPlayerViewModel.isPlayerInitialized) {
                musicPlayerViewModel.playNext()
            }        }

        repeatButton.setOnClickListener {
            musicPlayerViewModel.toggleRepeat()
        }

        musicPlayerViewModel.shuffleButtonImageRes.observe(viewLifecycleOwner) {
            shuffleButton.setImageResource(it)
        }
        shuffleButton.setOnClickListener {
            musicPlayerViewModel.toggleShuffle()
        }

        musicPlayerViewModel.repeatButtonImage.observe(viewLifecycleOwner) {
            repeatButton.setImageResource(it)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        seekBar = binding.seekBar
        progressDuration = binding.durationView
        progressPlaying = binding.progressView

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (musicPlayerViewModel.isPlayerInitialized && fromUser) {
                    musicPlayerViewModel.seekTo(progress.toLong())
                }
                Log.d("VideoPlayerFragment", "Seek bar progress changed to $progress")
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }


    override fun onStart() {
        super.onStart()
        musicPlayerViewModel.totalDuration.observe(viewLifecycleOwner) { duration ->
            seekBar.max = duration
            musicPlayerViewModel.currentPosition.observe(viewLifecycleOwner) { position ->
                progressDuration.text = formatDuration(duration)
                progressPlaying.text = formatDuration(position)
                if (!seekBar.isPressed) {
                    seekBar.progress = position
                }
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        super.onResume()
    }

    override fun onPause() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR;
        super.onPause()
    }

}