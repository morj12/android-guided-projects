package com.example.moviesapi.activity

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.moviesapi.adapters.ResultAdapter
import com.example.moviesapi.databinding.ActivityMainBinding
import com.example.moviesapi.model.Results
import com.example.moviesapi.viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity(), ResultAdapter.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var results: PagedList<Results>

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
        viewModel.pagedListLiveData.observe(this) {
            results = it
            fillRecyclerView()
        }
    }

    private fun fillRecyclerView() {

        adapter = ResultAdapter(this)
        adapter.submitList(results)

        val spanCount =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2
            else 4
        recyclerView = binding.recyclerView.also {
            it.layoutManager = GridLayoutManager(this, spanCount)
            it.itemAnimator = DefaultItemAnimator()
            it.adapter = adapter
        }

        adapter.notifyDataSetChanged()
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onItemClicked(item: Results) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("movie", item)
        startActivity(intent)
    }
}