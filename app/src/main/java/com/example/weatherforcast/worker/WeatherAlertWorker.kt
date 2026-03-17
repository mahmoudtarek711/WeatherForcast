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
import com.example.weatherforcast.ui.screens.AlarmActivity

class WeatherAlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "weather_alerts_channel"
        const val ALARM_CHANNEL_ID = "alarm_channel_v4"
        const val TAG = "WeatherWorker"
    }

    private val alertsDao = AppDatabase.getInstance(applicationContext).alertsDao()

    override suspend fun doWork(): Result {

        val isAlarm = inputData.getBoolean("IS_ALARM", false)
        val description = inputData.getString("WEATHER_DESC") ?: "Weather Alert!"
        val alertId = inputData.getLong("ALERT_ID", -1L)

        Log.d(TAG, "Worker started | Alarm: $isAlarm | AlertID: $alertId")

        return try {

            if (isAlarm) {
                triggerAlarm(description)
            } else {
                showSimpleNotification(description)
            }

            // ✅ Remove alarm from DB after triggering
            if (alertId != -1L) {
                alertsDao.deleteAlertById(alertId)
                Log.d(TAG, "Alert deleted from DB: $alertId")
            }

            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure()
        }
    }

    // -------------------- ALARM --------------------
    private fun triggerAlarm(description: String) {

        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createAlarmChannel(manager)

        val alarmIntent = Intent(applicationContext, AlarmActivity::class.java).apply {
            putExtra("WEATHER_DESC", description)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            applicationContext,
            1,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mainIntent = Intent(applicationContext, MainActivity::class.java)
        val mainPendingIntent = PendingIntent.getActivity(
            applicationContext,
            2,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, ALARM_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Weather Alarm")
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true) // 🔥 popup
            .setContentIntent(mainPendingIntent) // tap goes to Main
            .setOngoing(false) // can be dismissed
            .setAutoCancel(true)
            .build()

        manager.notify(1001, notification)

        // 🔥 Try launching activity directly (many devices require this)
        try {
            applicationContext.startActivity(alarmIntent)
            Log.d(TAG, "AlarmActivity launched directly")
        } catch (e: Exception) {
            Log.e(TAG, "Activity launch failed: ${e.message}")
        }
    }

    // -------------------- SIMPLE NOTIFICATION --------------------
    private fun showSimpleNotification(description: String) {

        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Weather Update")
            .setContentText(description)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .build()

        manager.notify(2001, notification)
    }

    // -------------------- ALARM CHANNEL --------------------
    private fun createAlarmChannel(manager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ALARM_CHANNEL_ID,
                "Critical Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null)
                enableVibration(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }
            manager.createNotificationChannel(channel)
        }
    }
}