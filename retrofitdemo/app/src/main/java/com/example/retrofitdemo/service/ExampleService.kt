package com.example.retrofitdemo.service

import com.example.retrofitdemo.model.Album
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ExampleService {

    @GET("albums")
    fun getResults(): Call<List<Album>>

}