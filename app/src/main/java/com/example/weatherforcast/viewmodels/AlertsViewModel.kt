package com.example.weatherforcast.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.model.AlertItem
import com.example.architechturestartercode.data.movie.repository.ForcastRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlertsViewModel(private val repository: ForcastRepository) : ViewModel() {

    // 1. Observe database changes as a StateFlow
    // The UI will automatically refresh whenever the Room table changes.
    val alerts: StateFlow<List<AlertItem>> = repository.getAllAlerts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. Add an alert
    fun addAlert(alert: AlertItem) {
        viewModelScope.launch {
            repository.saveAlert(alert)
        }
    }

    // 3. Remove an alert
    fun removeAlert(alert: AlertItem) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
        }
    }

    /** * 4. Helper for Undo Functionality
     * Since Room handles the ID, "restoring" an alert is technically just re-inserting it.
     * We use this to make the code in the Screen more readable.
     */
    fun restoreAlert(alert: AlertItem) {
        viewModelScope.launch {
            repository.saveAlert(alert)
        }
    }
}