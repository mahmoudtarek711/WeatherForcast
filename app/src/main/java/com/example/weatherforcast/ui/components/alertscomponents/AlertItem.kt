package com.example.weatherforcast.ui.components.alertscomponents
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class Alert(
    val id: Long,
    val from: String,
    val to: String,
    val type: AlertType
)

enum class AlertType {
    Alarm,
    Notification
}