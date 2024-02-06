package com.example.vibe.domain.usecases

import androidx.annotation.Keep
import com.example.vibe.domain.mappers.DataToSongMapper
import com.example.vibe.domain.models.Song
import com.example.vibe.data.repositories.MusicRepository
import javax.inject.Inject

class GetMusicDataUseCase @Inject constructor(
    private val musicRepository: MusicRepository,
    private val dataToSongMapper: DataToSongMapper
) {

    @Keep
    suspend operator fun invoke(query: String, key: String): List<Song> {
        val response = musicRepository.getData(query, key)
        val data = response.data
        return data.map { dataToSongMapper.map(it) }
    }
}