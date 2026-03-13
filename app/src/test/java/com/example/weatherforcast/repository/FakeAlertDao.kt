package com.example.weatherforcast.repository

import com.example.weatherforcast.datasource.local.AlertsDao
import com.example.weatherforcast.model.AlertItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAlertsDao : AlertsDao {
    private val list = mutableListOf<AlertItem>()
    private val flow = MutableStateFlow<List<AlertItem>>(emptyList())

    override fun getAllAlerts(): Flow<List<AlertItem>> = flow

    override suspend fun insertAlert(alert: AlertItem): Long {
        list.add(alert)
        flow.value = list.toList() // Update the flow so the test sees it
        return 0L
    }

    // You can leave these empty if you aren't testing them yet
    override suspend fun deleteAlert(alert: AlertItem) {}
    override suspend fun deleteAlertById(alertId: Long) {}
}