package com.example.top.database.artist

import androidx.room.*

@Dao
interface ArtistDao {
    @Query("SELECT * FROM Artist ORDER BY Artist.`order`") fun getAll(): List<Artist>

    @Insert fun insertAll(artists: List<Artist>)

    @Insert fun insert(artist: Artist)

    @Delete fun delete(artist: Artist)

    @Query("SELECT * FROM Artist WHERE Artist.id = :id") fun get(id: Long): Artist

    @Update fun update(artist: Artist)
}