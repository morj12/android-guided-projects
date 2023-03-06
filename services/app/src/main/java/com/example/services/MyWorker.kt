package com.example.services

import android.content.Context
import android.util.Log
import androidx.work.*

class MyWorker(val context: Context, private val workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    // Executed in another thread
    override fun doWork(): Result {
        val page = workerParameters.inputData.getInt(PAGE, 0)
        for (i in page until page + 10) {
            Thread.sleep(1000)
            log("Timer $i")
        }
        return Result.success()
    }

    private fun log(message: String) {
        Log.d("WORK_MANAGER", "${this::class.java.name} $message")
    }

    companion object {

        private const val PAGE = "page"
        const val WORK_NAME = "work_name"

        fun makeRequest(page: Int): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<MyWorker>().
                setInputData(workDataOf(PAGE to page))
                .setConstraints(makeConstraints())
                .build()
        }

        private fun makeConstraints() = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.METERED)
            .build()

    }

}