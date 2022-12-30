package com.example.moviesapi.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private var retrofit: Retrofit? = null

    private var BASE_URL = "https://api.themoviedb.org/3/"

    fun getService(): MovieApiService {

        if (retrofit == null)
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit!!.create(MovieApiService::class.java)
    }
}