package com.firdous.workmanagerdownload

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.*
import kotlinx.coroutines.delay

class WorkManagerDownload(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannel()
            }
            val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)
            notificationBuilder.addAction(android.R.drawable.ic_delete, "Cancel", intent)
            val notification = notificationBuilder.build()
            setForeground(ForegroundInfo(NOTIFICATION_ID, notification))
            for (i in 0..100) {
                setProgress(workDataOf(progress to i))
                showProgress(i)
                delay(100L)
            }
            Result.success()
        } catch (ex: Exception) {
            Log.e("Worker", ex.message ?: "Exception")
            Result.failure()
        }

    }

    private suspend fun showProgress(progress: Int) {
        val notification = notificationBuilder
            .setProgress(100, progress, false)
            .build()
        val foregroundInfo = ForegroundInfo(NOTIFICATION_ID, notification)
        setForeground(foregroundInfo)
    }

    private val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
        .setContentTitle(TITLE)
        .setTicker(TITLE)
        .setOngoing(true)
        .setContentText("Download File Progress")
        .setSmallIcon(androidx.core.R.drawable.notification_icon_background)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, TITLE, importance)
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val progress = "progress"
        const val NOTIFICATION_ID = 42
        const val CHANNEL_ID = "download_channel"
        const val TITLE = "Download File"
    }
}