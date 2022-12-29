package com.example.favourite_movies.model

import android.app.Application
import androidx.lifecycle.LiveData

object MovieRepository {

    private lateinit var movieDao: MovieDao

    // TODO: ???
    private lateinit var movies: LiveData<List<Movie>>

    fun initRepository(application: Application) {
        setMovieDao(MoviesDatabase.getDatabase(application).movieDao())
    }

    private fun setMovieDao(dao: MovieDao) {
        if (!::movieDao.isInitialized) movieDao = dao
    }

    fun getMovies(): LiveData<List<Movie>> {
        return movieDao.getAllMovies()
    }

    fun getGenreMovies(genreId: Long): LiveData<List<Movie>> {
        return movieDao.getGenreMovies(genreId)
    }

    fun insertMovie(movie: Movie) {
        movieDao.insert(movie)
    }

    fun updateMovie(movie: Movie) {
        movieDao.update(movie)
    }

    fun deleteMovie(movie: Movie) {
        movieDao.delete(movie)
    }

}