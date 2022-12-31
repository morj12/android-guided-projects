package com.example.didemo.model

import android.util.Log
import javax.inject.Inject

class Car @Inject constructor(var engine: Engine, var battery: Battery, var chassis: Chassis) {

    fun move() {
        Log.d("move()", "Car is moving")
    }

}
