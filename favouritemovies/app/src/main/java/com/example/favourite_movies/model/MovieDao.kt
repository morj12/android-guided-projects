package com.example.favourite_movies.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MovieDao {

    @Insert
    fun insert(movie: Movie)

    @Update
    fun update(movie: Movie)

    @Delete
    fun delete(movie: Movie)

    @Query(value = "SELECT * FROM Movie")
    fun getAllMovies(): LiveData<List<Movie>>

    @Query(value = "SELECT * FROM Movie WHERE genre_id == :genreId")
    fun getGenreMovies(genreId: Long): LiveData<List<Movie>>
}