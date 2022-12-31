package com.example.moviesapi.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.moviesapi.model.MovieDataSource
import com.example.moviesapi.model.MovieDataSourceFactory
import com.example.moviesapi.model.Results
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private var sourceLiveData: LiveData<MovieDataSource>
    private var executor: Executor
    var pagedListLiveData: LiveData<PagedList<Results>>
    private set

    init {
        val factory = MovieDataSourceFactory(application)
        sourceLiveData = factory.mutableLiveData
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(10)
            .setPageSize(20)
            .setPrefetchDistance(3)
            .build()

        executor = Executors.newCachedThreadPool()

        pagedListLiveData = LivePagedListBuilder(factory, config)
            .setFetchExecutor(executor)
            .build()
    }
}