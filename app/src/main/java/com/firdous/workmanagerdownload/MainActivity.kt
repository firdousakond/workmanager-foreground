package com.firdous.workmanagerdownload

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        startWorkManger()
    }

    private fun startWorkManger() {
        val inputData = Data.Builder().putInt("start", 0)
            .putInt("end", 100)
            .build()
        val workManager = WorkManager.getInstance(this)
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(WorkManagerDownload::class.java)
            .setInputData(inputData)
            .build()

        workManager.enqueueUniqueWork(
            "downloadImage",
            ExistingWorkPolicy.REPLACE,
            oneTimeWorkRequest
        )
        observeProgress(oneTimeWorkRequest)
    }

    private fun observeProgress(oneTimeWorkRequest: OneTimeWorkRequest) {
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(oneTimeWorkRequest.id)
            .observe(this) { workInfo ->
                if (workInfo != null) {
                    val progress = workInfo.progress
                    val value = progress.getInt(WorkManagerDownload.progress, 0)
                    Log.i("TAG", "$value")
                }
            }
    }
}