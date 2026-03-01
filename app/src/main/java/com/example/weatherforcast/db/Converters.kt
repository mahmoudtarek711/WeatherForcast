package com.example.weatherforcast.data.db

import androidx.room.TypeConverter
import com.example.weatherforcast.model.Response.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromWeatherList(value: List<Weather>): String = gson.toJson(value)

    @TypeConverter
    fun toWeatherList(value: String): List<Weather> {
        val type = object : TypeToken<List<Weather>>() {}.type
        return gson.fromJson(value, type)
    }

    // Repeat this pattern for other complex objects
    @TypeConverter
    fun fromMain(main: Main): String = gson.toJson(main)

    @TypeConverter
    fun toMain(value: String): Main = gson.fromJson(value, Main::class.java)

    @TypeConverter
    fun fromWind(wind: Wind): String = gson.toJson(wind)

    @TypeConverter
    fun toWind(value: String): Wind = gson.fromJson(value, Wind::class.java)

    @TypeConverter
    fun fromClouds(clouds: Clouds): String = gson.toJson(clouds)

    @TypeConverter
    fun toClouds(value: String): Clouds = gson.fromJson(value, Clouds::class.java)

    @TypeConverter
    fun fromSys(sys: Sys): String = gson.toJson(sys)

    @TypeConverter
    fun toSys(value: String): Sys = gson.fromJson(value, Sys::class.java)

    // Inside your Converters class
    @TypeConverter
    fun fromRain(rain: Rain?): String? {
        return if (rain == null) null else gson.toJson(rain)
    }

    @TypeConverter
    fun toRain(value: String?): Rain? {
        return if (value == null) null else gson.fromJson(value, Rain::class.java)
    }
}