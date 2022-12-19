package com.example.top.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.top.R
import com.example.top.adapter.ArtistAdapter
import com.example.top.adapter.OnItemClickListener
import com.example.top.database.AppDatabase
import com.example.top.database.artist.Artist
import com.example.top.database.artist.ArtistRepository
import com.example.top.databinding.ActivityMainBinding
import com.example.top.util.DefaultArtistsProvider
import com.example.top.util.Message

class MainActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ArtistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initToolbar()
        initAdapter()
        initRecyclerView()
        initFloatingActionButton()
        initDatabase()

//        initDefaultArtists()
    }

    private fun initDefaultArtists() =
        DefaultArtistsProvider.provideArtists().forEach {
            adapter.add(it)
            ArtistRepository.addArtist(it)
        }

    private fun initToolbar() = setSupportActionBar(binding.toolbar)

    private fun initAdapter() {
        adapter = ArtistAdapter(this)
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun initFloatingActionButton() {
        binding.fab.setOnClickListener {
            val intent = Intent(this, AddArtistActivity::class.java)
            intent.putExtra(Artist.ORDER, adapter.itemCount + 1)
            startActivityForResult(intent, 1)
        }
    }

    private fun initDatabase() {
        val artistDao = AppDatabase.getDatabase(this).artistDao()
        ArtistRepository.setDao(artistDao)
    }

    override fun onResume() {
        super.onResume()
        adapter.artistList = getArtistsFromDB()
        adapter.notifyDataSetChanged()
    }

    private fun getArtistsFromDB() = ArtistRepository.getAll().toMutableList()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(artist: Artist) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra(Artist.ID, artist.id)
        startActivity(intent)
    }

    override fun onLongItemClick(artist: Artist) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(100)
        }
        deleteArtist(artist)
    }

    private fun deleteArtist(artist: Artist) {
        AlertDialog.Builder(this)
            .setTitle(R.string.main_dialog_delete_title)
            .setMessage(getString(R.string.main_dialog_delete_message, artist.name))
            .setPositiveButton(R.string.details_dialog_delete_delete) { _, _ ->
                ArtistRepository.delete(artist)
                adapter.remove(artist)
                Message.showMessage(binding.containerMain, R.string.main_dialog_delete_success)
            }
            .setNegativeButton(R.string.label_dialog_cancel, null)
            .show()
    }

}