package com.example.services

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.services.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var id = 0

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.simpleService.setOnClickListener {
            startService(MyService.newIntent(this, 10))
        }

        binding.foregroundService.setOnClickListener {
            tryShowNotification()
        }
    }

    private fun tryShowNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this@MainActivity, POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_DENIED
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(POST_NOTIFICATIONS),
                    100
                )
            } else {
                showNotification()
            }
        } else {
            showNotification()
        }
    }

    private fun showNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Title")
            .setContentText("Text")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()

        notificationManager.notify(id++, notification)
    }

    companion object {

        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "channel_name"
    }
}