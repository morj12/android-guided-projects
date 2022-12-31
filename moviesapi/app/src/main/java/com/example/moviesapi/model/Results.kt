package com.example.moviesapi.model

import android.os.Parcelable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.moviesapi.R
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Results(

    @SerializedName("adult") val adult: Boolean,
    @SerializedName("backdrop_path") val backdropPath: String,
    @SerializedName("genre_ids") val genreIds: List<Int>,
    @SerializedName("id") val id: Int,
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("original_title") var originalTitle: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("popularity") val popularity: Double,
    @SerializedName("poster_path") var posterPath: String,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("title") val title: String,
    @SerializedName("video") val video: Boolean,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int

) : Parcelable {

    companion object {
        val CALLBACK = object : DiffUtil.ItemCallback<Results>() {
            override fun areItemsTheSame(oldItem: Results, newItem: Results): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Results, newItem: Results): Boolean {
                if ((oldItem.adult != newItem.adult)
                    || (oldItem.backdropPath != newItem.backdropPath)
                    || (oldItem.genreIds != newItem.genreIds)
                    || (oldItem.id != newItem.id)
                    || (oldItem.originalLanguage != newItem.originalLanguage)
                    || (oldItem.originalTitle != newItem.originalTitle)
                    || (oldItem.overview != newItem.overview)
                    || (oldItem.popularity != newItem.popularity)
                    || (oldItem.posterPath != newItem.posterPath)
                    || (oldItem.releaseDate != newItem.releaseDate)
                    || (oldItem.title != newItem.title)
                    || (oldItem.video != newItem.video)
                    || (oldItem.voteAverage != newItem.voteAverage)
                    || (oldItem.voteCount != newItem.voteCount)
                ) return false
                return true
            }
        }
    }
}

@BindingAdapter("posterPath")
fun loadImage(view: ImageView, url: String) {
    val completeUrl = view.context.getString(R.string.base_img_path) + url
    Glide.with(view.context)
        .load(completeUrl)
        .placeholder(R.drawable.ic_baseline_image_24)
        .into(view)
}
