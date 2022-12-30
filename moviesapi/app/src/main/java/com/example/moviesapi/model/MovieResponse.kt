package com.example.moviesapi.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieResponse(
    @SerializedName("page") val page : Int,
    @SerializedName("results") val results : List<Results>,
    @SerializedName("total_pages") val total_pages : Int,
    @SerializedName("total_results") val total_results : Int
) : Parcelable
