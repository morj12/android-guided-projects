package com.example.databindingdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.databindingdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var buttonsHandler: MainActivityButtonsHandler

    /**
     * Simple data binding
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.book = getCurrentBook()

        buttonsHandler = MainActivityButtonsHandler()
        binding.buttonsHandler = buttonsHandler
    }

    private fun getCurrentBook(): Book = Book("Hamlet", "Shakespeare")


    inner class MainActivityButtonsHandler {

        fun onOkClicked(view: View) {
            startActivity(Intent(this@MainActivity, OkActivity::class.java))
        }

        fun onCancelClicked(view: View) {
            startActivity(Intent(this@MainActivity, CancelActivity::class.java))
        }

    }
}