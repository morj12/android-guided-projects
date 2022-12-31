package com.example.moviesapi.service

import com.example.moviesapi.model.MovieResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {

    @GET("movie/popular")
    fun getPopularMoviesWithPaging(
        @Query("api_key") apiKey: String,
        @Query("page") page: Long
    ): Call<MovieResponse>

}