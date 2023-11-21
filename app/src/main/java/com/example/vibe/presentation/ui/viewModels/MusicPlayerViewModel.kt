package com.example.vibe.presentation.ui.viewModels

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.vibe.R
import com.example.vibe.domain.models.Song
import com.example.vibe.presentation.RepeatMode
import com.example.vibe.presentation.ShuffleMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MusicPlayerViewModel(application: Application) : AndroidViewModel(application) {

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun initPlayer() {
        checkPlayerInitialization()
        setUpPlayer()
        previousSongLink = _songLink.value
    }

    private val context = getApplication<Application>()

    lateinit var player: ExoPlayer

    private val _songLink = MutableLiveData<String>()
    private val songLink: LiveData<String> get() = _songLink

    private var previousSongLink: String? = null

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _currentPosition = MutableLiveData<Int>()
    private val _totalDuration = MutableLiveData<Int>()

    val currentPosition: LiveData<Int> get() = _currentPosition
    val totalDuration: LiveData<Int> get() = _totalDuration

    val isInitialized = ::player.isInitialized.not()

    private val searchSongList = mutableListOf<Song>()
    private val favoriteSongList = mutableListOf<Song>()
    private var isFavoritesListActive = false
    private var currentSongIndex = -1


    private val _repeatMode = MutableLiveData(RepeatMode.REPEAT_OFF)
    val repeatMode: LiveData<RepeatMode> get() = _repeatMode

    private val _shuffleMode = MutableLiveData(ShuffleMode.SHUFFLE_OFF)
    val shuffleMode: LiveData<ShuffleMode> get() = _shuffleMode
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

    private fun checkPlayerInitialization() {
        if (::player.isInitialized && _songLink.value != previousSongLink) {
            player.release()
        } else if (::player.isInitialized && _songLink.value == previousSongLink && isPlaying.value == true) {
            pause()
        } else if (::player.isInitialized && _songLink.value == previousSongLink && isPlaying.value?.not() == true) {
            play()
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun setUpPlayer() {
        if (!::player.isInitialized || _songLink.value != previousSongLink) {
            player = ExoPlayer.Builder(context)
                .setPauseAtEndOfMediaItems(false)
                .build()
            val mediaItem = MediaItem.Builder()
                .setUri(songLink.value)
                .setMimeType(MimeTypes.AUDIO_MP4)
                .build()

            player.setMediaItem(mediaItem)
            player.prepare()

            seekBarListener()
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
                                playNext()
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

    fun setPlayer(song: Song, index: Int) {
        val currentList = if (isFavoritesListActive) favoriteSongList else searchSongList
        if (index in currentList.indices) {
            currentSongIndex = index
        }

        coverUrl.value = song.cover
        _songLink.value = song.link
        title.value = song.title
        artist.value = song.artist
        initPlayer()

    }

    fun play() {
        player.play()
        _isPlaying.value = true
    }

    fun pause() {
        player.pause()
        _isPlaying.value = false
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
        val currentList = if (isFavoritesListActive) favoriteSongList else searchSongList
        if (_shuffleMode.value == ShuffleMode.SHUFFLE_ON) {
            currentSongIndex = (currentSongIndex + 1) % currentList.size
            currentList.shuffle()
        } else {
            if (currentSongIndex >= 0 && currentSongIndex < currentList.size - 1) {
                currentSongIndex++
            }
        }
        setPlayer(currentList[currentSongIndex], currentSongIndex)
    }

    fun playPrevious() {
        val currentList = if (isFavoritesListActive) favoriteSongList else searchSongList
        if (_shuffleMode.value == ShuffleMode.SHUFFLE_ON) {
            currentSongIndex =
                if (currentSongIndex > 0) currentSongIndex - 1 else currentList.size - 1
            currentList.shuffle()
        } else {
            if (currentSongIndex > 0 && currentSongIndex < currentList.size) {
                currentSongIndex--
            }
        }
        setPlayer(currentList[currentSongIndex], currentSongIndex)
    }

    val shuffleButtonImageRes = MutableLiveData<Int>()
    fun toggleShuffle() {
        _shuffleMode.value = when (_shuffleMode.value) {
            ShuffleMode.SHUFFLE_OFF -> {
                shuffleButtonImageRes.value = R.drawable.baseline_shuffle_on_24
                ShuffleMode.SHUFFLE_ON
            }
            ShuffleMode.SHUFFLE_ON -> {
                shuffleButtonImageRes.value = R.drawable.ic_shuffle
                ShuffleMode.SHUFFLE_OFF
            }
            else -> ShuffleMode.SHUFFLE_ON
        }
    }

    fun toggleRepeat() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.REPEAT_OFF -> RepeatMode.REPEAT_ALL
            RepeatMode.REPEAT_ALL -> RepeatMode.REPEAT_ONE
            RepeatMode.REPEAT_ONE -> RepeatMode.REPEAT_OFF
            else -> {
                return
            }
        }
    }

    fun repeat() {
        when (_repeatMode.value) {
            RepeatMode.REPEAT_OFF -> player.repeatMode = Player.REPEAT_MODE_OFF
            RepeatMode.REPEAT_ALL -> player.repeatMode = Player.REPEAT_MODE_ALL
            RepeatMode.REPEAT_ONE -> player.repeatMode = Player.REPEAT_MODE_ONE
            else -> return
        }
    }

    private val sharedPreferences =
        application.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    val isSleepTimerEnabled: LiveData<Boolean>
    private val _isSleepTimerEnabled = MutableLiveData<Boolean>()
    private val _isSleepTimerTriggered = MutableLiveData<Boolean>()
    val isSleepTimerTriggered: LiveData<Boolean> = _isSleepTimerTriggered


    init {
        _isSleepTimerEnabled.value = sharedPreferences.getBoolean("SLEEP_TIMER_ENABLED", false)
        isSleepTimerEnabled = _isSleepTimerEnabled
        handleSleepTimer(_isSleepTimerEnabled.value ?: false)
    }

    fun setSleepTimer(isEnabled: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean("SLEEP_TIMER_ENABLED", isEnabled)
            apply()
        }
        _isSleepTimerEnabled.value = isEnabled
        handleSleepTimer(isEnabled)
    }

    private var sleepTimerJob: Job? = null

    private fun handleSleepTimer(isEnabled: Boolean) {
        if (isEnabled && isPlaying.value == true) {
            sleepTimerJob = viewModelScope.launch(Dispatchers.IO) {
                try {
                    Log.d("MusicPlayerViewModel", "Sleep timer started")
                    delay(1 * 5 * 1000) // 5 seconds for testing purposes
                    Log.d("MusicPlayerViewModel", "Time's up - stopping playback")
                    withContext(Dispatchers.Main) {
                        pause()
                        _isSleepTimerTriggered.postValue(true)
                    }
                } catch (e: Exception) {
                    Log.e("MusicPlayerViewModel", "Error in sleep timer: ${e.message}")
                }
            }
        } else {
            sleepTimerJob?.cancel()
            _isSleepTimerTriggered.postValue(false)
        }
    }

    override fun onCleared() {
        if (::player.isInitialized) {
            player.release()
        }
        super.onCleared()
    }

    fun seekTo(progress: Long) {
        player.seekTo(progress)
    }

}

