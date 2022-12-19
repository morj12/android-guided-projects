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
import com.example.top.adapter.ActorAdapter
import com.example.top.adapter.OnItemClickListener
import com.example.top.database.AppDatabase
import com.example.top.database.actor.Actor
import com.example.top.database.actor.ActorRepository
import com.example.top.databinding.ActivityMainBinding
import com.example.top.util.DefaultActorsProvider
import com.example.top.util.Message

class MainActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ActorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initToolbar()
        initAdapter()
        initRecyclerView()
        initFloatingActionButton()
        initDatabase()

//        initDefaultActors()
    }

    private fun initDefaultActors() =
        DefaultActorsProvider.provideActors().forEach {
            adapter.add(it)
            ActorRepository.addActor(it)
        }

    private fun initToolbar() = setSupportActionBar(binding.toolbar)

    private fun initAdapter() {
        adapter = ActorAdapter(this)
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun initFloatingActionButton() {
        binding.fab.setOnClickListener {
            val intent = Intent(this, AddActorActivity::class.java)
            intent.putExtra(Actor.ORDER, adapter.itemCount + 1)
            startActivityForResult(intent, 1)
        }
    }

    private fun initDatabase() {
        val actorDao = AppDatabase.getDatabase(this).actorDao()
        ActorRepository.setDao(actorDao)
    }

    override fun onResume() {
        super.onResume()
        adapter.actorList = getActorsFromDB()
        adapter.notifyDataSetChanged()
    }

    private fun getActorsFromDB() = ActorRepository.getAll().toMutableList()

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

    override fun onItemClick(actor: Actor) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra(Actor.ID, actor.id)
        startActivity(intent)
    }

    override fun onLongItemClick(actor: Actor) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(100)
        }
        deleteActor(actor)
    }

    private fun deleteActor(actor: Actor) {
        AlertDialog.Builder(this)
            .setTitle(R.string.main_dialog_delete_title)
            .setMessage(getString(R.string.main_dialog_delete_message, actor.name))
            .setPositiveButton(R.string.details_dialog_delete_delete) { _, _ ->
                ActorRepository.delete(actor)
                adapter.remove(actor)
                Message.showMessage(binding.containerMain, R.string.main_dialog_delete_success)
            }
            .setNegativeButton(R.string.label_dialog_cancel, null)
            .show()
    }

}