package com.example.services

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobWorkItem
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.services.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var pageNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.simpleService.setOnClickListener {
            startService(MyService.newIntent(this, 10))
            stopService(MyForegroundService.newIntent(this)) // to stop service from outside
        }

        binding.foregroundService.setOnClickListener {
            requestPermissionsAndExecute {
                ContextCompat.startForegroundService(this, MyForegroundService.newIntent(this))
            }
        }

        binding.intentService.setOnClickListener {
            ContextCompat.startForegroundService(this, MyIntentService.newIntent(this))
        }

        binding.jobScheduler.setOnClickListener {
            val componentName = ComponentName(this, MyJobService::class.java)

            val jobInfo = JobInfo.Builder(MyJobService.JOB_ID, componentName)
                .setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
//                .setPersisted(true)
                .build()

            val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = MyJobService.newIntent(getAndIncreasePage())
                jobScheduler.enqueue(jobInfo, JobWorkItem(intent))
//                jobScheduler.schedule(jobInfo) // kills all other services
            } else {
                startService(MyIntentService2.newIntent(this, getAndIncreasePage()))
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(POST_NOTIFICATIONS),
                100
            )
        }
    }

    private fun getAndIncreasePage(): Int {
        pageNumber += 10
        return pageNumber
    }

    private fun requestPermissionsAndExecute(method: () -> Unit) {
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
                method()
            }
        } else {
            method()
        }
    }
}