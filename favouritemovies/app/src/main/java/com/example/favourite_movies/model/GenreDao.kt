package com.example.favourite_movies.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GenreDao {

    @Insert
    fun insert(genre: Genre)

    @Update
    fun update(genre: Genre)

    @Delete
    fun delete(genre: Genre)

    @Query(value = "SELECT * FROM Genre")
    fun getAllGenres(): LiveData<List<Genre>>

}