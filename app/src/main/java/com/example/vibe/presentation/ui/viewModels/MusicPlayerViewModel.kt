package com.example.vibe.presentation.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.vibe.R
import com.example.vibe.domain.models.Song
import com.example.vibe.presentation.RepeatMode
import com.example.vibe.presentation.ShuffleMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val player: ExoPlayer
) : ViewModel() {

    private val _songLink = MutableLiveData<String?>()
    private val songLink: LiveData<String?> get() = _songLink

    private var previousSongLink: String? = null

    var isPlayerInitialized = false

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _currentPosition = MutableLiveData<Int>()
    private val _totalDuration = MutableLiveData<Int>()

    val currentPosition: LiveData<Int> get() = _currentPosition
    val totalDuration: LiveData<Int> get() = _totalDuration

    private val searchSongList = mutableListOf<Song>()
    private val favoriteSongList = mutableListOf<Song>()
    var isFavoritesListActive = false
    private var currentSongIndex = -1


    private val _repeatMode = MutableLiveData(RepeatMode.REPEAT_OFF)
    val repeatMode: LiveData<RepeatMode> get() = _repeatMode

    private val _shuffleMode = MutableLiveData(ShuffleMode.SHUFFLE_OFF)
    private val shuffleMode: LiveData<ShuffleMode> get() = _shuffleMode
    val shuffleButtonImageRes = MutableLiveData<Int>()
    val repeatButtonImage = MutableLiveData<Int>()
    private val shuffledSongList = mutableListOf<Song>()

    val coverUrl = MutableLiveData<String>()
    val title = MutableLiveData<String>()
    val artist = MutableLiveData<String>()
    val formattedTitle: String
        get() {
            return if (title.value != null && artist.value != null) {
                "${artist.value} - ${title.value}"
            } else {
                "Nothing is on play"
            }
        }

    fun setSongToPlayer(song: Song, index: Int) {
        val currentList = if (isFavoritesListActive) favoriteSongList else searchSongList
        if (index in currentList.indices) {
            currentSongIndex = index
        }

        coverUrl.value = song.cover
        _songLink.value = song.link
        title.value = song.title
        artist.value = song.artist

        setUpPlayer()
        handlePlayerState()

        previousSongLink = _songLink.value
    }

    private fun setUpPlayer() {
        if (!isPlayerInitialized || songLink.value != previousSongLink) {
            val mediaItem = MediaItem.Builder()
                .setUri(songLink.value)
                .setMimeType(MimeTypes.AUDIO_MP4)
                .build()

            player.setMediaItem(mediaItem)
            player.prepare()

            isPlayerInitialized = true

            seekBarListener()
            play()
        }
    }

    private fun handlePlayerState() {
        if (isPlayerInitialized && _songLink.value == previousSongLink && isPlaying.value == true) {
            pause()
        } else if (isPlayerInitialized && _songLink.value == previousSongLink && isPlaying.value?.not() == true) {
            play()
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun seekBarListener() {
        player.addListener(object : Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _totalDuration.value = player.duration.toInt()
                    _currentPosition.value = player.currentPosition.toInt()

                }
                if (playbackState == Player.STATE_READY && playWhenReady) {
                    viewModelScope.launch {
                        while (true) {
                            _currentPosition.value = player.currentPosition.toInt()
                            delay(1000)
                            val currentPosition = _currentPosition.value ?: 0
                            val totalDuration = _totalDuration.value ?: 0
                            val tolerance = 1000 // 1 second tolerance
                            if (currentPosition >= totalDuration - tolerance && currentPosition <= totalDuration + tolerance) {
                                if (repeatMode.value != RepeatMode.REPEAT_ONE) {
                                    playNext()
                                } else {
                                    _currentPosition.value = 0
                                    repeatOne()
                                }
                            }
                        }
                    }
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onPositionDiscontinuity(reason: Int) {
                if (reason == Player.EVENT_POSITION_DISCONTINUITY) {
                    _currentPosition.value = player.currentPosition.toInt()
                }
            }
        })
    }

    fun play() {
        player.play()
        _isPlaying.value = true
    }

    fun pause() {
        player.pause()
        _isPlaying.value = false
    }

    fun seekTo(progress: Long) {
        player.seekTo(progress)
    }

    fun setSongList(songs: List<Song>) {
        searchSongList.clear()
        searchSongList.addAll(songs)
        isFavoritesListActive = false
    }

    fun setFavSongList(songs: List<Song>) {
        favoriteSongList.clear()
        favoriteSongList.addAll(songs)
        isFavoritesListActive = true
    }

    fun playNext() {
        val currentList = if (shuffleMode.value == ShuffleMode.SHUFFLE_ON) shuffledSongList
        else if (isFavoritesListActive) favoriteSongList
        else searchSongList

        if (currentSongIndex < currentList.size - 1) {
            currentSongIndex++
        } else {
            if (repeatMode.value == RepeatMode.REPEAT_ALL) {
                currentSongIndex = 0
            } else {
                return
            }
        }

        setSongToPlayer(currentList[currentSongIndex], currentSongIndex)

    }

    fun playPrevious() {
        val currentList = if (shuffleMode.value == ShuffleMode.SHUFFLE_ON) shuffledSongList
        else if (isFavoritesListActive) favoriteSongList
        else searchSongList

        if (currentSongIndex > 0) {
            currentSongIndex--
        } else {
            if (repeatMode.value == RepeatMode.REPEAT_ALL) {
                currentSongIndex = currentList.size - 1
            } else {
                return
            }
        }
        setSongToPlayer(currentList[currentSongIndex], currentSongIndex)

    }


    fun repeatOne() {
        player.seekTo(0)
        player.play()
        _isPlaying.value = true
    }

    fun toggleShuffle() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                _shuffleMode.value = when (_shuffleMode.value) {
                    ShuffleMode.SHUFFLE_OFF -> {
                        withContext(Dispatchers.IO) {
                            shuffledSongList.clear()
                            val currentList =
                                if (isFavoritesListActive) favoriteSongList else searchSongList
                            shuffledSongList.addAll(currentList)
                            shuffledSongList.shuffle()
                        }
                        shuffleButtonImageRes.value = R.drawable.baseline_shuffle_on_24
                        ShuffleMode.SHUFFLE_ON
                    }

                    ShuffleMode.SHUFFLE_ON -> {
                        withContext(Dispatchers.IO){
                            val currentSong = if (currentSongIndex in shuffledSongList.indices)
                                shuffledSongList[currentSongIndex]
                            else null
                            val originalList =
                                if (isFavoritesListActive) favoriteSongList else searchSongList
                            currentSongIndex =
                                originalList.indexOfFirst { it == currentSong }.coerceAtLeast(0)
                            shuffledSongList.clear()
                        }
                        shuffleButtonImageRes.value = R.drawable.ic_shuffle
                        ShuffleMode.SHUFFLE_OFF
                    }

                    else -> ShuffleMode.SHUFFLE_OFF
                }
            }
        }
    }


    fun toggleRepeat() {
        if (currentSongIndex != -1 && favoriteSongList.isNotEmpty() || searchSongList.isNotEmpty()) {
            _repeatMode.value = when (_repeatMode.value) {
                RepeatMode.REPEAT_OFF -> {
                    repeatButtonImage.value = R.drawable.baseline_repeat_on_24
                    RepeatMode.REPEAT_ALL
                }

                RepeatMode.REPEAT_ALL -> {
                    repeatButtonImage.value = R.drawable.baseline_repeat_one_24
                    RepeatMode.REPEAT_ONE
                }

                RepeatMode.REPEAT_ONE -> {
                    repeatButtonImage.value = R.drawable.baseline_repeat_24
                    RepeatMode.REPEAT_OFF
                }

                else -> {
                    RepeatMode.REPEAT_OFF
                }
            }
        } else return
    }

    override fun onCleared() {
        player.release()
        super.onCleared()
    }

}

