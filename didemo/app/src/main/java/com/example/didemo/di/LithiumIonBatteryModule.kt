package com.example.didemo.di

import com.example.didemo.model.Battery
import com.example.didemo.model.LithiumIonBattery
import dagger.Module
import dagger.Provides

@Module
object LithiumIonBatteryModule {

    @Provides
    fun provideBattery(battery: LithiumIonBattery): Battery {
        battery.logBatteryType()
        return battery
    }
}