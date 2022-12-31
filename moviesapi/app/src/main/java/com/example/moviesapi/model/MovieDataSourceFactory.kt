package com.example.moviesapi.model

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.DataSource.Factory

class MovieDataSourceFactory(
    private var application: Application
) : Factory<Long, Results>() {

    var mutableLiveData = MutableLiveData<MovieDataSource>()
    private lateinit var movieDataSource: MovieDataSource

    override fun create(): DataSource<Long, Results> {
        movieDataSource = MovieDataSource(application)
        mutableLiveData.postValue(movieDataSource)
        return movieDataSource
    }
}