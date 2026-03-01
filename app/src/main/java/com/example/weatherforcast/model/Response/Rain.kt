package com.example.weatherforcast.model.Response

import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("3h")
    val volume: Double
)