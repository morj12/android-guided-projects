package com.example.favourite_movies

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.favourite_movies.databinding.ActivityMainBinding
import com.example.favourite_movies.model.Genre
import com.example.favourite_movies.model.Movie
import com.example.favourite_movies.viewmodel.MainActivityViewModel
import java.util.concurrent.Executors
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), MovieAdapter.OnItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var selectedGenre: Genre

    private var genreList = mutableListOf<Genre>()
    private var movieList = mutableListOf<Movie>()

    private var executor = Executors.newSingleThreadExecutor()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieAdapter

    private lateinit var selectedMovie: Movie

    companion object {
        const val ADD_MOVIE_RQ = 111
        const val EDIT_MOVIE_RQ = 222
    }

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
        binding.fab.setOnClickListener {
            val intent = Intent(this, AddEditActivity::class.java)
            startActivityForResult(intent, ADD_MOVIE_RQ)
        }
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

        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val movieToDelete = movieList[viewHolder.adapterPosition]
                executor.execute { viewModel.deleteMovie(movieToDelete) }
            }
        }).attachToRecyclerView(recyclerView)
    }

    override fun onItemClick(movie: Movie) {
        selectedMovie = movie
        val intent = Intent(this, AddEditActivity::class.java)
        intent.putExtra("id", movie.id)
        intent.putExtra("name", movie.name)
        intent.putExtra("description", movie.description)
        startActivityForResult(intent, EDIT_MOVIE_RQ)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_MOVIE_RQ) {
                val movie = Movie(
                    _name = data?.getStringExtra("name")!!,
                    _description = data.getStringExtra("description")!!,
                    _genreId = selectedGenre.id
                )
                executor.execute { viewModel.addMovie(movie) }
            } else if (requestCode == EDIT_MOVIE_RQ) {
                selectedMovie.name = data?.getStringExtra("name")!!
                selectedMovie.description = data.getStringExtra("description")!!

                executor.execute { viewModel.updateMovie(selectedMovie) }

            }
        }

    }

}