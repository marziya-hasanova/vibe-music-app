package com.example.vibe.domain.mappers

interface Mapper <P, R> {

    fun map(params: P): R
}