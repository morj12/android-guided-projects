package com.example.cart_and_notes.presentation.view.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.cart_and_notes.R
import com.example.cart_and_notes.databinding.ActivityMainBinding
import com.example.cart_and_notes.presentation.view.FragmentManager

// TODO: use use-cases
// TODO: set bottom navigation selected item to the correct one after adding new item
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        binding.bottomNav.selectedItemId = R.id.notes
        FragmentManager.setFragment(NoteFragment.newInstance(), this)
        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.setting -> Log.d("BOTTOM_NAV", "Clicked on settings")
                R.id.notes -> FragmentManager.setFragment(NoteFragment.newInstance(), this)
                R.id.cart -> FragmentManager.setFragment(CartFragment.newInstance(), this)
                R.id.new_item -> FragmentManager.currentFragment?.onClickNew()
            }
            true
        }
    }
}