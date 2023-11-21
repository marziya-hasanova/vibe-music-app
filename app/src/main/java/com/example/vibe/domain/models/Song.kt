package com.example.vibe.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Song(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val title: String,
    val artist: String,
    val cover: String,
    val link: String,
    val duration: Int,
    var isFavorite: Boolean = false

){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Song) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

