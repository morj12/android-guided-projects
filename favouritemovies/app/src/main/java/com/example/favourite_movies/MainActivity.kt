package com.example.favourite_movies

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.favourite_movies.databinding.ActivityMainBinding
import com.example.favourite_movies.model.Genre
import com.example.favourite_movies.model.Movie
import com.example.favourite_movies.viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity(), MovieAdapter.OnItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var selectedGenre: Genre

    private var genreList = mutableListOf<Genre>()
    private var movieList = mutableListOf<Movie>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
            .create(MainActivityViewModel::class.java)

        viewModel.getGenresData().observe(
            this
        ) {
            genreList = it as MutableList<Genre>
            showInSpinner()
        }

        initFab()
        initSpinner()
    }

    private fun showInSpinner() {
        val adapter = ArrayAdapter(this, R.layout.spinner_item, genreList)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        binding.spinnerAdapter = adapter
    }

    private fun initFab() {
        binding.fab.setOnClickListener {}
    }

    private fun initSpinner() {
        binding.spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                selectedGenre = adapterView?.getItemAtPosition(position) as Genre
                val message = selectedGenre.toString()

                loadGenreMoviesInList(selectedGenre.id)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun loadGenreMoviesInList(genreId: Long) {
        viewModel.getGenreMoviesData(genreId).observe(
            this
        ) {
            movieList = it as MutableList<Movie>
            initRecyclerView()
        }
    }

    private fun initRecyclerView() {
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        adapter = MovieAdapter(this)
        adapter.setMovieList(movieList)
        recyclerView.adapter = adapter
    }

    override fun onItemClick(movie: Movie) {

    }

}