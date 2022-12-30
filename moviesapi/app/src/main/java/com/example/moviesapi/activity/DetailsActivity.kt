package com.example.moviesapi.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.moviesapi.R
import com.example.moviesapi.databinding.ActivityDetailsBinding
import com.example.moviesapi.model.Results

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var movie: Results

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("movie")) {
            movie = intent.getParcelableExtra("movie")!!
            setImageInformation()
        }
    }

    private fun setImageInformation() {
        val imgPath = getString(R.string.base_img_path) + movie.poster_path
        Glide.with(this)
            .load(imgPath)
            .placeholder(R.drawable.ic_baseline_image_24)
            .into(binding.ivDetail)

        binding.tvTitleDetail.text = movie.title
        binding.tvPopularityDetail.text = movie.popularity.toString()
        binding.tvDescriptionDetail.text = movie.overview
    }
}