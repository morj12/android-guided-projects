package com.example.counter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.counter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        initViews()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        val countLiveData = viewModel.getCurrentValue()
        countLiveData.observe(this) { binding.tvCounter.text = it.toString() }
    }

    private fun initViews() {
        binding.tvCounter.text = viewModel.getCurrentValue().toString()
        binding.btLess.setOnClickListener(::decreaseValue)
        binding.btMore.setOnClickListener(::increaseValue)
    }

    private fun increaseValue(view: View) {
        viewModel.getIncreasedValue()
    }

    private fun decreaseValue(view: View) {
        viewModel.getDecreasedValue()
    }
}