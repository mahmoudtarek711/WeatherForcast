package com.example.architechturestartercode.data.movie.datasource.remote

import com.example.weatherforcast.model.Response.ForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ForcastService {

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): ForecastResponse

}