package com.example.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import android.os.PersistableBundle
import android.util.Log
import kotlinx.coroutines.*

class MyJobService : JobService() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            coroutineScope.launch {
                var workItem = params?.dequeueWork()
                while (workItem != null) {
                    val page = workItem.intent.getIntExtra(PAGE, 0)
                    for (i in page..page + 10) {
                        delay(1000)
                        log("Timer $i")
                    }
                    params?.completeWork(workItem)
                    workItem = params?.dequeueWork()
                }
                // True -> recreate service when stopped manually
                jobFinished(params, false)
            }
        }
        /**
         * True -> service is being executed
         * False -> service has finished its job
         */
        return true
    }

    /**
     * True -> recreate service when stopped by the system
     */
    override fun onStopJob(params: JobParameters?) = true

    private fun log(message: String) {
        Log.d("JOB_SERVICE", "${this::class.java.name} $message")
    }

    companion object {

        const val JOB_ID = 222
        private const val PAGE = "page"

        fun newIntent(page: Int): Intent {
            return Intent().apply {
                putExtra(PAGE, page)
            }
        }

    }

}