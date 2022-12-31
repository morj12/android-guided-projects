package com.example.didemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.didemo.di.ChassisModule
import com.example.didemo.di.DaggerCarComponent
import com.example.didemo.model.Car
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var car: Car

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * Used only if there are no parameters
         *
         * val component = DaggerCarComponent.create()
         */

        /**
         * Used if there is any parameter
         */
        val component = DaggerCarComponent.builder().chassisModule(ChassisModule(500)).build()
        component.inject(this)

        car.move()
    }
}