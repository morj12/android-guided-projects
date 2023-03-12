package com.example.cart_and_notes.presentation.view.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.cart_and_notes.R
import com.example.cart_and_notes.databinding.ActivityMainBinding
import com.example.cart_and_notes.presentation.view.FragmentManager

// TODO: use use-cases
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var currentTab = R.id.notes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.setting -> Log.d("BOTTOM_NAV", "Clicked on settings")
                R.id.notes -> {
                    FragmentManager.setFragment(NoteFragment.newInstance(), this)
                    currentTab = R.id.notes
                }
                R.id.cart -> {
                    FragmentManager.setFragment(CartFragment.newInstance(), this)
                    currentTab = R.id.cart
                }
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_any_item -> FragmentManager.currentFragment?.onClickNew()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNav.selectedItemId = currentTab
    }
}