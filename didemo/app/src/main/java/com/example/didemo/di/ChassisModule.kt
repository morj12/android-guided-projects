package com.example.didemo.di

import com.example.didemo.model.Chassis
import dagger.Module
import dagger.Provides

/**
 * Used to provide objects from classes which cannot be modified. For example, Retrofit.
 */

@Module
class ChassisModule(var weight: Int) {

    @Provides
    fun provideChassis() = Chassis(weight)
}