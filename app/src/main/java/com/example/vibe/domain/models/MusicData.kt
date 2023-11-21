package com.example.vibe.domain.models

data class MusicData(
    val `data`: List<Data>,
    val next: String,
    val total: Int
)