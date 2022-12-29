package com.example.favourite_movies.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.favourite_movies.model.Genre
import com.example.favourite_movies.model.GenreRepository
import com.example.favourite_movies.model.Movie
import com.example.favourite_movies.model.MovieRepository

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var genres: LiveData<List<Genre>>
    lateinit var genreMovies: LiveData<List<Movie>>

    init {
        GenreRepository.initRepository(application)
        MovieRepository.initRepository(application)
    }

    fun getGenresData(): LiveData<List<Genre>> {
        genres = GenreRepository.getGenres()
        return genres
    }

    fun getGenreMoviesData(genreId: Long): LiveData<List<Movie>> {
        genreMovies = MovieRepository.getGenreMovies(genreId)
        return genreMovies
    }

    fun addNewMovie(movie: Movie) {
        MovieRepository.insertMovie(movie)
    }

    fun updateMovie(movie: Movie) {
        MovieRepository.updateMovie(movie)
    }

    fun deleteMovie(movie: Movie) {
        MovieRepository.deleteMovie(movie)
    }


}