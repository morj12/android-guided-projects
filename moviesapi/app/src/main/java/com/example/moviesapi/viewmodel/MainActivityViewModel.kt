package com.example.moviesapi.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.moviesapi.model.MovieRepository
import com.example.moviesapi.model.Results

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private var context = application

    fun getAllMovieData(): LiveData<List<Results>> {
        return MovieRepository.getMovies(context)
    }

}