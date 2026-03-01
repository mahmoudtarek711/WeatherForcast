package com.example.weatherforcast.model.Response

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast_table")
data class ForecastItem(
    @PrimaryKey
    val dt: Long, // Using dt as the unique ID for the forecast timestamp
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val sys: Sys,
    val dt_txt: String,
    val rain: Rain? = null
)