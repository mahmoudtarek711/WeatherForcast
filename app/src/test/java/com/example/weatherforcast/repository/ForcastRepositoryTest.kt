package com.example.weatherforcast.repository

import app.cash.turbine.test
import com.example.architechturestartercode.data.movie.datasource.local.ForcastDao
import com.example.architechturestartercode.data.movie.datasource.local.ForcastLocalDataSource
import com.example.architechturestartercode.data.movie.datasource.remote.ForcastRemoteDataSource
import com.example.architechturestartercode.data.movie.repository.ForcastRepository
import com.example.weatherforcast.datasource.local.AlertsDao
import com.example.weatherforcast.model.AlertItem
import com.example.weatherforcast.model.Response.ForecastResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Test

class ForcastRepositoryTest {

    private lateinit var repository: ForcastRepository
    private lateinit var remoteDataSource: ForcastRemoteDataSource
    private lateinit var localDataSource: FakeLocalDataSource
    private lateinit var alertsDao: AlertsDao
    private lateinit var forcastDao: ForcastDao

    @Before
    fun setup() {
        remoteDataSource =mockk(relaxed = true)
        localDataSource = mockk(relaxed = true)
        alertsDao = mockk(relaxed = true)
        forcastDao = mockk(relaxed = true)
        repository = ForcastRepository(remoteDataSource, localDataSource, alertsDao)
    }


    @Test
    fun getStoredForecast_3Forecast_countof3() = runTest {
        val fakeLocal = FakeLocalDataSource()

        val repository = ForcastRepository(
            remoteDataSource = mockk(),
            localDataSource = fakeLocal,
            alertsDao = mockk()
        )

        // When: Making the actual actions (Inserting 3 items)
        for (i in 1..3) {
            repository.saveForecastItem(item = mockk())
        }

        // Then: The assert part
        val resultList = repository.getStoredForecasts().first()

        assertThat(resultList.size, `is`(3))
    }

    @Test
    fun delete_2Forecasts_countof1() = runTest {
        val fakeLocal = FakeLocalDataSource()

        val repository = ForcastRepository(
            remoteDataSource = mockk(),
            localDataSource = fakeLocal,
            alertsDao = mockk()
        )
        var fakeItem: ForecastResponse = mockk()

        // When: Making the actual actions (Inserting 2 items)
        repository.saveForecastItem(item = fakeItem)
        repository.saveForecastItem(item = mockk())

        // Then: The assert part
        repository.deleteForecastItem(fakeItem)
        val resultList = repository.getStoredForecasts().first()

        assertThat(resultList.size, `is`(1))
    }


    @Test
    fun getAllAlerts_addTwoAlerts_returnsCountOfTwo() = runTest {
        // 1. Given: Setup
        val alertsDao = FakeAlertsDao()
        val repository = ForcastRepository(
            remoteDataSource = mockk(),
            localDataSource = mockk(),
            alertsDao = alertsDao
        )

        // 2. When: Adding 2 alert items
        repository.saveAlert(mockk())
        repository.saveAlert(mockk())

        // 3. Then: Assert
        val resultList = repository.getAllAlerts().first()

        assertThat(resultList.size, `is`(2))
    }







    @Test
    fun saveForcastItem_forecastItem_truecalled() = runTest {
        // Given
        val forcastItem: ForecastResponse = mockk()

        // When
        repository.saveForecastItem(forcastItem)


        // Then
        coVerify { localDataSource.insertForcastItem(forcastItem) }

    }

    @Test
    fun deleteForcastItem_forecastItem_truecalled() = runTest {
        // Given
        val forcastItem: ForecastResponse = mockk()

        // When
        repository.deleteForecastItem(forcastItem)


        // Then
        coVerify { localDataSource.deleteForcastItem(forcastItem) }

    }



}