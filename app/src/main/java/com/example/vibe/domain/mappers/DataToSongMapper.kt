package com.example.vibe.domain.mappers

import com.example.vibe.domain.mappers.Mapper
import com.example.vibe.domain.models.Data
import com.example.vibe.domain.models.Song
import javax.inject.Inject

class DataToSongMapper @Inject constructor() : Mapper<Data, Song> {
    override fun map(params: Data): Song {
        return Song(
            title = params.title,
            artist = params.artist.name,
            cover = params.album.cover_medium,
            link = params.preview,
            duration = params.duration,
            id = params.id
        )
    }
}