package com.example.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*

class MyService: Service() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val start = intent?.getIntExtra(EXTRA_START, 0) ?: 0
        coroutineScope.launch {
            for (i in start..start + 100) {
                delay(1000)
                log("Timer $i")
            }
        }


        /**
         * START_STICKY -> recreate service if it was destroyed by system
         * START_NOT_STICKY -> not recreate if it was destroyed by system
         * START_REDELIVER_INTENT -> recreate with the same intent
         */
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun log(message: String) {
        Log.d("SIMPLE_SERVICE", "${this::class.java.name} $message")
    }

    companion object {

        private const val EXTRA_START = "start"

        fun newIntent(context: Context, start: Int): Intent {
            return Intent(context, MyService::class.java).apply {
                putExtra(EXTRA_START, start)
            }
        }

    }

}