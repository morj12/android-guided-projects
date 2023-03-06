package com.example.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService

class MyJobIntentService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        val page = intent.getIntExtra(PAGE, 0)
        for (i in page until page + 10) {
            Thread.sleep(1000)
            log("Timer $i")
        }
    }

    private fun log(message: String) {
        Log.d("JOB_INTENT_SERVICE", "${this::class.java.name} $message")
    }

    companion object {

        private const val PAGE = "page"
        private const val JOB_ID = 333

        private fun newIntent(context: Context, page: Int): Intent {
            return Intent(context, MyJobIntentService::class.java).apply {
                putExtra(PAGE, page)
            }
        }

        fun enqueue(context: Context, page: Int) = enqueueWork(
            context,
            MyJobIntentService::class.java,
            JOB_ID,
            newIntent(context, page)
        )

    }
}