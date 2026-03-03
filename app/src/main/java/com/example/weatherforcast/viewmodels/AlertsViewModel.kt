package com.example.weatherforcast.ui.viewmodels

import com.example.weatherforcast.worker.WeatherAlertWorker
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.architechturestartercode.data.movie.repository.ForcastRepository
import com.example.weatherforcast.model.AlertItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

// Inherit from AndroidViewModel to get application context safely
class AlertsViewModel(
    application: Application,
    private val repository: ForcastRepository
) : AndroidViewModel(application) {
    private val TAG = "test"

    val alerts: StateFlow<List<AlertItem>> = repository.getAllAlerts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    fun addAlert(alert: AlertItem, weatherDescription: String) {
        viewModelScope.launch {
            try {
                val generatedId = repository.saveAlert(alert)
                val delayInMillis = calculateDelayInMillis(alert.fromHour, alert.fromMinute)

                val alertWorkRequest = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
                    .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
                    .addTag("alert_$generatedId")
                    .setInputData(workDataOf(
                        "ALERT_ID" to generatedId,
                        "IS_ALARM" to alert.isAlarm,
                        "WEATHER_DESC" to weatherDescription // Passing the description here
                    ))
                    .build()

                WorkManager.getInstance(getApplication()).enqueue(alertWorkRequest)
                Log.d("test", "Alert Scheduled: $weatherDescription")
            } catch (e: Exception) {
                Log.e("test", "Failed to add alert: ${e.message}")
            }
        }
    }

    fun removeAlert(alert: AlertItem) {
        viewModelScope.launch {
            // Cancel pending work if manually removed
            WorkManager.getInstance(getApplication()).cancelAllWorkByTag("alert_${alert.id}")
            repository.deleteAlert(alert)
        }
    }

    fun restoreAlert(alert: AlertItem,desc:String) {
        viewModelScope.launch {
            addAlert(alert,desc) // Re-schedules work and saves to DB
        }
    }

    private fun calculateDelayInMillis(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0) // Reset millis for precision

        // If the time has already passed today, schedule for tomorrow
        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Return the raw difference in milliseconds
        return calendar.timeInMillis - now
    }
}