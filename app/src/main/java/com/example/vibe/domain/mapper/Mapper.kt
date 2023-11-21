package com.example.vibe.domain.mapper

interface Mapper <P, R> {

    fun map(params: P): R
}