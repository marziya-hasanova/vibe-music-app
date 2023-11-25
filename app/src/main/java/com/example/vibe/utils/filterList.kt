package com.example.vibe.utils

import com.example.vibe.domain.models.Song
import java.text.Normalizer

fun filterList(
    favorites: List<Song>,
    filteredList:MutableList<Song>,
    text: String?)
{
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
}
