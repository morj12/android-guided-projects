package com.example.didemo.model

import android.util.Log
import javax.inject.Inject

class LithiumIonBattery @Inject constructor(): Battery {
    override fun logBatteryType() {
        Log.d("Battery", "This a lithium-ion battery")
    }
}