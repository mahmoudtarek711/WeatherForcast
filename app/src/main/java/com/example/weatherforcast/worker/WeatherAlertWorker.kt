package com.example.weatherforcast.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.architechturestartercode.data.db.AppDatabase
import com.example.weatherforcast.MainActivity
import com.example.weatherforcast.R
import com.example.weatherforcast.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherAlertWorker(
    private val context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "weather_alerts_channel"
        const val ALARM_CHANNEL_ID = "alarm_channel"
        val TAG = "test"
    }

    private val alertsDao = AppDatabase.getInstance(context).alertsDao()

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val alertId = inputData.getLong("ALERT_ID", -1L)
        val isAlarm = inputData.getBoolean("IS_ALARM", false)
        // Get the dynamic description passed from ViewModel
        val weatherDesc = inputData.getString("WEATHER_DESC") ?: "Check your weather forecast now!"

        Log.d(TAG, "Worker started for Alert ID: $alertId, isAlarm: $isAlarm")

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
            Log.e(TAG, "Error in WeatherAlertWorker: ${e.message}", e)
            Result.failure()
        }
    }

    private fun getBaseNotificationBuilder(channelId: String, title: String, description: String): NotificationCompat.Builder {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // Your App Logo
            .setContentTitle(title)
            .setContentText(description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .setColor(ContextCompat.getColor(context, RainTeal.toArgb()))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showSimpleNotification(description: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(CHANNEL_ID, "Weather Alerts", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val notification = getBaseNotificationBuilder(CHANNEL_ID, "Weather Update", description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun triggerAlarmNotification(description: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(ALARM_CHANNEL_ID, "Critical Alarms", NotificationManager.IMPORTANCE_HIGH).apply {
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null)
        }
        notificationManager.createNotificationChannel(channel)

        val notification = getBaseNotificationBuilder(ALARM_CHANNEL_ID, "WEATHER ALARM", description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            // Use full screen intent for the alarm mode
            .setFullScreenIntent(getBaseNotificationBuilder(ALARM_CHANNEL_ID, "", "").build().contentIntent, true)
            .build()

        notificationManager.notify(1001, notification)
    }
}