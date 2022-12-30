package com.example.moviesapi.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.moviesapi.R
import com.example.moviesapi.service.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MovieRepository {

    private var results = mutableListOf<Results>()
    private var resultLiveData = MutableLiveData<List<Results>>()

    fun getMovies(context: Context): MutableLiveData<List<Results>> {
        val service = RetrofitInstance.getService()
        val call = service.getPopularMovies(context.getString(R.string.apikey))
        call.enqueue(callBack)
        return resultLiveData
    }

    private var callBack = object: Callback<MovieResponse> {
        override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
            val body = response.body()
            if (body != null) {
                results = body.results.toMutableList()
                resultLiveData.value = results
            }
        }

        override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
            Log.e("RETROFIT FAILURE", t.message.toString())
        }

    }
}