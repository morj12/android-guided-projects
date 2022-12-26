package com.example.databindingdemo

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.databindingdemo.databinding.ActivityOkBinding

/**
 * Data binding using included content file
 */

class OkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_ok)

        setSupportActionBar(binding.toolbar)
        binding.book = getCurrentBook()

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun getCurrentBook(): Book = Book("Hamlet", "Shakespeare")
}