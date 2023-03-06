package com.example.services

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class MyIntentService: IntentService(NAME) {
    
    /**
     * If it's called more than once, each instance is executed consequentially
     */
    override fun onCreate() {
        super.onCreate()
        setIntentRedelivery(true) // recreate service
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    /**
     * Executed in another thread
     */
    override fun onHandleIntent(intent: Intent?) {
        for (i in 0..100) {
            Thread.sleep(1000)
            log("Timer $i")
        }
        // Stops automatically
    }

    private fun log(message: String) {
        Log.d("INTENT_SERVICE", "${this::class.java.name} $message")
    }

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Intent service")
        .setContentText("is being executed")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .build()

    companion object {

        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "channel_name"
        private const val NOTIFICATION_ID = 229
        private const val NAME = "MyIntentService"

        fun newIntent(context: Context): Intent {
            return Intent(context, MyService::class.java)
        }

    }
}