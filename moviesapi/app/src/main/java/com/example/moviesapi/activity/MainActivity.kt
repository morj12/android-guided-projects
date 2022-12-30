package com.example.moviesapi.activity

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telecom.Call.Details
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.moviesapi.R
import com.example.moviesapi.adapters.ResultAdapter
import com.example.moviesapi.databinding.ActivityMainBinding
import com.example.moviesapi.model.MovieResponse
import com.example.moviesapi.model.Results
import com.example.moviesapi.service.RetrofitInstance
import com.example.moviesapi.viewmodel.MainActivityViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), ResultAdapter.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var movies = mutableListOf<Results>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ResultAdapter

    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        initSwipeRefreshLayout()
        getPopularMovies()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
            .create(MainActivityViewModel::class.java)
    }

    private fun initSwipeRefreshLayout() {
        swipeRefreshLayout = binding.srl
        swipeRefreshLayout.setColorSchemeResources(androidx.appcompat.R.color.primary_dark_material_dark)
        swipeRefreshLayout.setOnRefreshListener(::getPopularMovies)
    }

    private fun getPopularMovies() {
        viewModel.getAllMovieData().observe(
            this
        ) {
            movies = it.toMutableList()
            fillRecyclerView()
        }
    }

    private fun fillRecyclerView() {
        recyclerView = binding.recyclerView
        adapter = ResultAdapter(this)
        adapter.setList(movies)

        val spanCount =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2
            else 4
        recyclerView.layoutManager = GridLayoutManager(this, spanCount)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onItemClicked(item: Results) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("movie", item)
        startActivity(intent)
    }
}