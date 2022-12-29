package com.example.favourite_movies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.favourite_movies.databinding.ActivityAddEditBinding
import com.example.favourite_movies.model.Movie

class AddEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditBinding
    private lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movie = Movie(0, "", "", 0)
        binding.movie = movie
        initOkayButton()

        if (intent.hasExtra("id")) {
            title = "Edit movie"
            val movieName = intent.getStringExtra("name")
            movie.name = movieName!!
            val movieDescription = intent.getStringExtra("description")
            movie.description = movieDescription!!
        } else title = "Add movie"
    }

    private fun initOkayButton() {
        binding.btOk.setOnClickListener {
            if (movie.name.isNotEmpty()) {
                val intent = Intent()
                intent.putExtra("name", movie.name)
                intent.putExtra("description", movie.description)
                setResult(RESULT_OK, intent)
                finish()
            } else Toast.makeText(this, "Movie name can't be empty", Toast.LENGTH_SHORT).show()
        }
    }
}