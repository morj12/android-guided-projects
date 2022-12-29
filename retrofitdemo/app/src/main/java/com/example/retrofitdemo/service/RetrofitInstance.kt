package com.example.retrofitdemo.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private var retrofit: Retrofit? = null

    private var BASE_URL = "https://jsonplaceholder.typicode.com/"

    fun getService(): ExampleService {

        if (retrofit == null)
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit!!.create(ExampleService::class.java)
    }

}