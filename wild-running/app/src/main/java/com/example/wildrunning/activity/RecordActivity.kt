package com.example.wildrunning.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wildrunning.R
import com.example.wildrunning.activity.MainActivity.Companion.mainContext
import com.example.wildrunning.adapter.RunAdapter
import com.example.wildrunning.data.Run
import com.example.wildrunning.databinding.ActivityRecordBinding
import com.example.wildrunning.utils.FirestoreClient
import com.google.firebase.firestore.Query

class RecordActivity : AppCompatActivity() {

    private var selectedSport = SportType.Running

    private lateinit var binding: ActivityRecordBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var runsList: MutableList<Run>
    private lateinit var mAdapter: RunAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_record)
        setSupportActionBar(toolbar)

        toolbar.title = getString(R.string.bar_title_record)
        toolbar.setTitleTextColor(getColor(this, R.color.gray_dark))
        toolbar.setBackgroundColor(getColor(this, R.color.gray_light))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        recyclerView = binding.rvRecords
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        runsList = mutableListOf()
        mAdapter = RunAdapter(runsList)
        recyclerView.adapter = mAdapter
    }

    override fun onResume() {
        super.onResume()
        loadRecyclerView("date", Query.Direction.DESCENDING)
    }

    override fun onPause() {
        super.onPause()
        runsList.clear()
    }

    private fun loadRecyclerView(field: String, order: Query.Direction) {
        runsList.clear()
        FirestoreClient.loadRuns(field, order, selectedSport, runsList, mAdapter)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.order_records_by, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val order: Query.Direction

        when (item.itemId) {
            R.id.orderby_date -> {
                item.title = if (item.title == getString(R.string.orderby_dateZA)) {
                    order = Query.Direction.DESCENDING
                    getString(R.string.orderby_dateAZ)
                } else {
                    order = Query.Direction.ASCENDING
                    getString(R.string.orderby_dateZA)
                }
                loadRecyclerView("date", order)
            }
            R.id.orderby_duration -> {
                item.title = if (item.title == getString(R.string.orderby_durationZA)) {
                    order = Query.Direction.DESCENDING
                    getString(R.string.orderby_durationAZ)
                } else {
                    order = Query.Direction.ASCENDING
                    getString(R.string.orderby_durationZA)
                }
                loadRecyclerView("duration", order)
            }
            R.id.orderby_distance -> {
                item.title = if (item.title == getString(R.string.orderby_distanceZA)) {
                    order = Query.Direction.DESCENDING
                    getString(R.string.orderby_distanceAZ)
                } else {
                    order = Query.Direction.ASCENDING
                    getString(R.string.orderby_distanceZA)
                }
                loadRecyclerView("distance", order)
            }
            R.id.orderby_avgspeed -> {
                item.title = if (item.title == getString(R.string.orderby_avgspeedZA)) {
                    order = Query.Direction.DESCENDING
                    getString(R.string.orderby_avgspeedAZ)
                } else {
                    order = Query.Direction.ASCENDING
                    getString(R.string.orderby_avgspeedZA)
                }
                loadRecyclerView("avgSpeed", order)
            }
            R.id.orderby_maxspeed -> {
                item.title = if (item.title == getString(R.string.orderby_maxspeedZA)) {
                    order = Query.Direction.DESCENDING
                    getString(R.string.orderby_maxspeedAZ)
                } else {
                    order = Query.Direction.ASCENDING
                    getString(R.string.orderby_maxspeedZA)
                }
                loadRecyclerView("maxSpeed", order)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun onLoadSportClicked(view: View) {
        selectedSport = SportType.valueOf(view.tag.toString())

        binding.ivBike.setBackgroundColor(getColor(mainContext, R.color.gray_medium))
        binding.ivRollerSkate.setBackgroundColor(getColor(mainContext, R.color.gray_medium))
        binding.ivRunning.setBackgroundColor(getColor(mainContext, R.color.gray_medium))

        when (selectedSport) {
            SportType.Bike -> binding.ivBike.setBackgroundColor(
                getColor(
                    mainContext,
                    R.color.orange
                )
            )
            SportType.RollerSkate -> binding.ivRollerSkate.setBackgroundColor(
                getColor(
                    mainContext,
                    R.color.orange
                )
            )
            SportType.Running -> binding.ivRunning.setBackgroundColor(
                getColor(
                    mainContext,
                    R.color.orange
                )
            )
        }

        loadRecyclerView("date", Query.Direction.DESCENDING)
    }
}