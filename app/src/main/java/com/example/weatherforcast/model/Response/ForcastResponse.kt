package com.example.weatherforcast.model.Response

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast_table")
data class ForecastResponse(
    @PrimaryKey val id: Int, // We will map city.id to this in the ViewModel
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastItem>,
    val city: City
)