package com.example.weatherforcast.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.architechturestartercode.data.movie.repository.ForcastRepository
import com.example.weatherforcast.data.local.SettingsManager
import com.example.weatherforcast.model.Response.ForecastResponse
import com.example.weatherforcast.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: ForcastRepository) : ViewModel() {
    private val _favoritesState = MutableStateFlow<UiState<List<ForecastResponse>>>(UiState.Loading)
    val favoritesState = _favoritesState.asStateFlow()


    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            repository.getStoredForecasts().collect { list ->
                _favoritesState.value = if (list.isEmpty()) UiState.Error("No favorites yet")
                else UiState.Success(list)
            }
        }
    }

    fun addFavorite(item: ForecastResponse) {
        viewModelScope.launch { repository.saveForecastItem(item) }
    }

    fun deleteFavorite(item: ForecastResponse) {
        viewModelScope.launch { repository.deleteForecastItem(item) }
    }
    fun addFavoriteByLocation(lat: Double, lon: Double, apiKey: String,lang: String) {
        viewModelScope.launch {
            repository.getRemoteForecast(lat, lon, apiKey,lang)
                .collect { response ->
                    // Ensure the response has the ID set for Room's PrimaryKey
                    val favorite = response.copy(id = response.city.id)
                    repository.saveForecastItem(favorite)
                }
        }
    }
}
