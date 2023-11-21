//package com.example.vibe.domain
//
//import com.example.vibe.data.db.FavoriteSongDao
//import com.example.vibe.data.repositories.FavoriteSongRepository
//import com.example.vibe.domain.mapper.SongToFavSongMapper
//import com.example.vibe.domain.models.FavSong
//import com.example.vibe.domain.models.Song
//import kotlinx.coroutines.flow.Flow
//
//class GetFavSongUseCase(
//    private val repository: FavoriteSongRepository,
//    private val mapper: SongToFavSongMapper
//) {
//
//    suspend fun addToFavorites(params: Song): FavSong {
//        val favSong = mapper.map(params)
//        repository.addToFavorites(favSong)
//
//        return repository.findByLink(favSong.link!!);
//    }
//
//    suspend fun removeFromFavorites(params: Song): FavSong {
//
//        val realFavSong = repository.findByLink(params.link)
//        repository.deleteFromFavorites(realFavSong)
//
//        return realFavSong
//    }
//
//    suspend fun getAllFavSongs(): Flow<List<FavSong>> {
//        val favSongList = repository.getAllFavorites();
//        return favSongList
//    }
//
//}