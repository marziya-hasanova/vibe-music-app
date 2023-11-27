package com.example.vibe.domain.interactors

import com.example.vibe.domain.usecases.GetMusicDataUseCase
import com.example.vibe.domain.models.Song
import javax.inject.Inject

class MusicInteractor @Inject constructor(
    private val getMusicDataUseCase: GetMusicDataUseCase
) {

    suspend fun getMusicData(query: String, key: String): List<Song> {
        return getMusicDataUseCase(query, key)
    }
}
