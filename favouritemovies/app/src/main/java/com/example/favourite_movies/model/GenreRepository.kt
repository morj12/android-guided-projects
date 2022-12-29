package com.example.favourite_movies.model

import android.app.Application
import androidx.lifecycle.LiveData

object GenreRepository {

    private lateinit var genreDao: GenreDao

    // TODO: ???
    private lateinit var genres: LiveData<List<Genre>>

    fun initRepository(application: Application) {
        setGenreDao(MoviesDatabase.getDatabase(application).genreDao())
    }

    private fun setGenreDao(dao: GenreDao) {
        if (!::genreDao.isInitialized) genreDao = dao
    }

    fun getGenres(): LiveData<List<Genre>> {
        return genreDao.getAllGenres()
    }

    fun insertGenre(genre: Genre) {
        genreDao.insert(genre)
    }

    fun updateGenre(genre: Genre) {
        genreDao.update(genre)
    }

    fun deleteGenre(genre: Genre) {
        genreDao.delete(genre)
    }

}