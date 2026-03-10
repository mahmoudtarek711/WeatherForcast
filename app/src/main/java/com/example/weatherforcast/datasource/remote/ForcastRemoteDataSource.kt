package com.example.architechturestartercode.data.movie.datasource.remote
import com.example.weatherforcast.model.Response.ForecastResponse
import com.example.weatherforcast.network.Network
import kotlinx.coroutines.flow.Flow

class ForcastRemoteDataSource {
    private val forecastService: ForcastService = Network.forcastService

    suspend fun getAllMovies(lat: Double,long: Double,apikey:String,lang:String): ForecastResponse {
       return forecastService.getForecast(lat = lat, lon = long, apiKey = apikey, lang = lang)
    }
}

