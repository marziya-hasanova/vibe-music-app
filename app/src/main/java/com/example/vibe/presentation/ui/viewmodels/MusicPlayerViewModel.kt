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
import com.example.vibe.utils.NOTHING_ON_PLAY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val player: ExoPlayer
) : ViewModel() {

    init {
        player.addListener(object : Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        _totalDuration.value = player.duration.toInt()
                        if (playWhenReady) {
                            updateCurrentPosition()
                        }
                    }

                    Player.STATE_BUFFERING -> {
                    }

                    Player.STATE_ENDED -> {
                        handlePlaybackEnded()
                    }

                    Player.STATE_IDLE -> {
                    }
                }
            }


            @Deprecated("Deprecated in Java")
            override fun onPositionDiscontinuity(reason: Int) {
                if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                    _currentPosition.value = player.currentPosition.toInt()
                }
            }
        })
    }

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
    val shuffleMode: LiveData<ShuffleMode> = _shuffleMode

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
                NOTHING_ON_PLAY
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
    }

    fun setFavSongList(songs: List<Song>) {
        favoriteSongList.clear()
        favoriteSongList.addAll(songs)
    }

    fun setShuffledList() {
        val favs = favoriteSongList
        val list = searchSongList
        if (isFavoritesListActive) {
            shuffledSongList.clear()
            shuffledSongList.addAll(favs.shuffled())
        } else {
            shuffledSongList.clear()
            shuffledSongList.addAll(list.shuffled())

        }

    }

    fun playNext() {
        val currentList = if (_shuffleMode.value == ShuffleMode.SHUFFLE_ON) shuffledSongList
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
        val currentList = if (_shuffleMode.value == ShuffleMode.SHUFFLE_ON) shuffledSongList
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


    private fun repeatOne() {
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
                            setShuffledList()
                        }
                        shuffleButtonImageRes.value = R.drawable.baseline_shuffle_on_24
                        ShuffleMode.SHUFFLE_ON
                    }

                    ShuffleMode.SHUFFLE_ON -> {
                        withContext(Dispatchers.IO) {
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

    private fun handlePlaybackEnded() {
        when (_repeatMode.value) {
            RepeatMode.REPEAT_ALL -> {
                if (currentSongIndex < getCurrentSongList().size - 1) {
                    currentSongIndex++
                } else {
                    currentSongIndex = 0
                }
                playCurrentSong()
            }

            RepeatMode.REPEAT_ONE -> {
                repeatOne()
            }

            else -> {
                val currentPosition = _currentPosition.value ?: 0
                val totalDuration = _totalDuration.value ?: 0
                val tolerance = 1000
                if (currentPosition >= totalDuration - tolerance && currentPosition <= totalDuration + tolerance) {
                    playNext()
                }
            }
        }
    }

    private fun playCurrentSong() {
        val currentList = getCurrentSongList()
        if (currentSongIndex in currentList.indices) {
            setSongToPlayer(currentList[currentSongIndex], currentSongIndex)
        }
    }

    private fun getCurrentSongList(): List<Song> {
        return when {
            _shuffleMode.value == ShuffleMode.SHUFFLE_ON -> shuffledSongList
            isFavoritesListActive -> favoriteSongList
            else -> searchSongList
        }
    }

    private fun updateCurrentPosition() {
        viewModelScope.launch {
            while (player.playbackState == Player.STATE_READY && player.playWhenReady) {
                _currentPosition.value = player.currentPosition.toInt()
                delay(1000)
            }
        }
    }

    override fun onCleared() {
        player.release()
        super.onCleared()
    }

}

