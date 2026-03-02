package com.example.weatherforcast.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class AlertItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Unique ID for each alert
    val fromHour: Int,
    val fromMinute: Int,
    val toHour: Int,
    val toMinute: Int,
    val isAlarm: Boolean
)