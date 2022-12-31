package com.example.moviesapi.model

import android.app.Application
import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.example.moviesapi.R
import com.example.moviesapi.service.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDataSource(
    private val application: Application,
) : PageKeyedDataSource<Long, Results>() {

    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<Long, Results>
    ) {
        val movieApiService = RetrofitInstance.getService()
        val call = movieApiService.getPopularMoviesWithPaging(
            application.getString(R.string.apikey),
            1
        )
        call.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                val body = response.body()
                if (body != null) {
                    val results = body.results.toMutableList()
                    callback.onResult(results, null, 2)
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.e("RETROFIT FAILURE", t.message.toString())
            }
        })
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, Results>) {}

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, Results>) {
        val movieApiService = RetrofitInstance.getService()
        val call = movieApiService.getPopularMoviesWithPaging(
            application.getString(R.string.apikey),
            params.key
        )
        call.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                val body = response.body()
                if (body != null) {
                    val results = body.results.toMutableList()
                    callback.onResult(results, params.key + 1)
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.e("RETROFIT FAILURE", t.message.toString())
            }
        })
    }
}