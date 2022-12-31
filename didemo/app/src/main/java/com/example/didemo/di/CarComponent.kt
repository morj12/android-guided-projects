package com.example.didemo.di

import com.example.didemo.MainActivity
import dagger.Component

@Component(modules = [ChassisModule::class, LithiumIonBatteryModule::class])
interface CarComponent {
    fun inject(mainActivity: MainActivity)
}