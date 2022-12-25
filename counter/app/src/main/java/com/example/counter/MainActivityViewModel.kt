package com.example.counter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel(
    private var count: Int = 1,
    private var countLiveData: MutableLiveData<Int> = MutableLiveData()
) : ViewModel() {

    fun getDecreasedValue() {
        --count
        countLiveData.value = count
    }

    fun getIncreasedValue() {
        ++count
        countLiveData.value = count
    }

    fun getCurrentValue(): MutableLiveData<Int> {
        countLiveData.value = count
        return countLiveData
    }
}