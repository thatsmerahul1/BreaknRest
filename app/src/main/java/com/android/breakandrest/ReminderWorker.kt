package com.android.breakandrest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

class ReminderWorker(appContext: Context, workerParams: WorkerParameters): CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("ReminderWorker", "doWork started")
        try {
            if (isWithinWorkHours()) {
                Log.d("ReminderWorker", "Within work hours, sending notification")
                sendNotification()
            } else {
                Log.d("ReminderWorker", "Outside work hours, not sending notification")
            }
            return Result.success()
        } catch (e: Exception) {
            Log.e("ReminderWorker", "Error in doWork(): ${e.message}", e)
            return Result.retry()
        }
    }

    private suspend fun isWithinWorkHours(): Boolean {
        val repository = WorkHoursRepository(applicationContext)
        val workHours = repository.getSettings().workHours

        val now = LocalTime.now()
        val startTime = LocalTime.parse(workHours.startTime)
        val endTime = LocalTime.parse(workHours.endTime)

        val withinWorkHours = !now.isBefore(startTime) && !now.isAfter(endTime)
        Log.d("ReminderWorker", "Current time: $now, Start time: $startTime, End time: $endTime, Within work hours: $withinWorkHours")

        return withinWorkHours
    }


    private fun sendNotification() {
        createNotificationChannel()

        val soundUri = Uri.parse("android.resource://${applicationContext.packageName}/raw/notification_sound")

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Replace with your app icon
            .setContentTitle("Stand Up Reminder")
            .setContentText("It's time to stand up and stretch!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(soundUri)

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ++NOTIFICATION_ID
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Stand Up Reminder"
            val descriptionText = "Notifications to remind you to stand up at regular intervals."
            val importance = NotificationManager.IMPORTANCE_HIGH

            val soundUri = Uri.parse("android.resource://${applicationContext.packageName}/raw/notification_sound")
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setSound(soundUri, audioAttributes)
            }

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private val CHANNEL_ID = "StandUpReminderChannel"
        private var NOTIFICATION_ID = 0
    }
}