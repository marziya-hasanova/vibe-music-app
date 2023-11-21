package com.example.vibe.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.vibe.domain.models.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteSongDao {

    @Query("SELECT * FROM favorites")
    fun getAll(): Flow<List<Song>>

    @Insert
    @Transaction
    fun insert(favSong: Song)

    @Delete
    @Transaction
    fun delete(favSong: Song)

}