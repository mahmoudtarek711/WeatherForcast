package com.example.weatherforcast.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.architechturestartercode.data.db.AppDatabase
import com.example.weatherforcast.MainActivity
import com.example.weatherforcast.R
import com.example.weatherforcast.ui.theme.RainTeal
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherAlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "weather_alerts_channel"
        const val ALARM_CHANNEL_ID = "alarm_channel"
        const val TAG = "WeatherWorker"
    }

    private val alertsDao =
        AppDatabase.getInstance(applicationContext).alertsDao()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        val alertId = inputData.getLong("ALERT_ID", -1L)
        val isAlarm = inputData.getBoolean("IS_ALARM", false)
        val weatherDesc =
            inputData.getString("WEATHER_DESC") ?: "Check your weather forecast"

        Log.d(TAG, "Worker started for Alert ID: $alertId | Alarm: $isAlarm")

        try {

            if (isAlarm) {
                triggerAlarmNotification(weatherDesc)
            } else {
                showSimpleNotification(weatherDesc)
            }

            if (alertId != -1L) {
                alertsDao.deleteAlertById(alertId)
            }

            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Worker error: ${e.message}")
            Result.failure()
        }
    }

    // ----------------------------------------------------
    // BASE BUILDER
    // ----------------------------------------------------

    private fun getBaseNotificationBuilder(
        channelId: String,
        title: String,
        description: String
    ): NotificationCompat.Builder {

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .setColor(RainTeal.toArgb())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
    }

    // ----------------------------------------------------
    // SIMPLE NOTIFICATION
    // ----------------------------------------------------

    private fun showSimpleNotification(description: String) {

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        createNotificationChannel(
            notificationManager,
            CHANNEL_ID,
            "Weather Alerts"
        )

        val notification = getBaseNotificationBuilder(
            CHANNEL_ID,
            "Weather Update",
            description
        ).build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    // ----------------------------------------------------
    // ALARM NOTIFICATION
    // ----------------------------------------------------

    private fun triggerAlarmNotification(description: String) {

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        createAlarmChannel(notificationManager)

        // Normal tap intent
        val normalIntent = Intent(applicationContext, MainActivity::class.java)

        val normalPendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            normalIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Full screen intent
        val fullScreenIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            applicationContext,
            1,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, ALARM_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Weather Alarm")
            .setContentText(description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .setColor(RainTeal.toArgb())
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(normalPendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .build()

        notificationManager.notify(1001, notification)
    }

    // ----------------------------------------------------
    // CHANNELS
    // ----------------------------------------------------

    private fun createNotificationChannel(
        manager: NotificationManager,
        id: String,
        name: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                id,
                name,
                NotificationManager.IMPORTANCE_HIGH
            )

            manager.createNotificationChannel(channel)
        }
    }

    private fun createAlarmChannel(manager: NotificationManager) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                ALARM_CHANNEL_ID,
                "Critical Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    null
                )
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            manager.createNotificationChannel(channel)
        }
    }
}