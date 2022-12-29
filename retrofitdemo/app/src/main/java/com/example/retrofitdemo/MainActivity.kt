package com.example.retrofitdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitdemo.adapters.AlbumAdapter
import com.example.retrofitdemo.databinding.ActivityMainBinding
import com.example.retrofitdemo.model.Album
import com.example.retrofitdemo.service.RetrofitInstance
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var albums = mutableListOf<Album>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAlbums()
    }

    private fun getAlbums() {
        val service = RetrofitInstance.getService()
        val call = service.getResults()
        call.enqueue(object: retrofit2.Callback<List<Album>> {
            override fun onResponse(call: Call<List<Album>>, response: Response<List<Album>>) {
                albums = response.body()?.toMutableList() ?: mutableListOf()
                initRecyclerView()

            }

            override fun onFailure(call: Call<List<Album>>, t: Throwable) {

            }
        })
    }

    private fun initRecyclerView() {
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AlbumAdapter()
        adapter.setList(albums)
        recyclerView.adapter = adapter
    }
}