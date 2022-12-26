package com.example.databindingdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.databindingdemo.databinding.ActivityCancelBinding

/**
 * Two-way data binding using BaseObservable class
 */

class CancelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCancelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancel)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_cancel)
        binding.greeting = getCurrentGreeting()
    }

    private fun getCurrentGreeting() = Greeting("John", "Hello!")
}