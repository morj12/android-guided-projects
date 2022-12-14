package com.example.sports

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sports.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var listAdapter: SportListAdapter
    private lateinit var adapter: SportAdapter

    /**
     * No need of controlling each element -> use ListAdapter
     * Total control -> use Adapter
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        getAllSports()
    }

    private fun setupRecyclerView() {
        listAdapter = SportListAdapter(this)
        adapter = SportAdapter(this)
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = listAdapter
            adapter = this@MainActivity.adapter
        }
    }

    private fun sports(): MutableList<Sport> {
        val soccer =
            Sport(1, "Soccer", "https://www.mgpu.ru/wp-content/uploads/2018/10/futbol1.jpg")
        val hockey =
            Sport(2, "Hockey", "https://wallpapercave.com/wp/wp2194988.jpg")
        val tennis =
            Sport(3, "Tennis", "https://kto-chto-gde.ru/wp-content/uploads/2016/11/7455.jpeg")
        val pingPong =
            Sport(4, "Ping pong", "https://sopka.ru/photos/ill/eaaf1f0565c5e0c417f73a539ea808a2.jpeg")
        val golf =
            Sport(5, "Golf", "https://s1.1zoom.ru/big3/524/Golf_Fields_Ball_Legs_Lawn_515191_4992x3328.jpg")
        return mutableListOf(soccer, hockey, tennis, pingPong, golf)
    }

    private fun getAllSports() {
        val sportsData = sports()
//        listAdapter.submitList(sportsData)
        sportsData.forEach {
            adapter.add(it)
        }
    }

    override fun onClick(sport: Sport) {
        Snackbar.make(binding.root, sport.name, Snackbar.LENGTH_SHORT).show()
    }
}