package com.example.weatherforcast.viewmodels

import android.app.Application
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import app.cash.turbine.test
import com.example.architechturestartercode.data.movie.repository.ForcastRepository
import com.example.weatherforcast.model.AlertItem
import com.example.weatherforcast.ui.UiState
import com.example.weatherforcast.ui.viewmodels.AlertsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.verify


@OptIn(ExperimentalCoroutinesApi::class)
class AlertsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AlertsViewModel
    private val repository: ForcastRepository = mockk(relaxed = true)
    private val application: Application = mockk(relaxed = true)
    private val workManager: WorkManager = mockk(relaxed = true)

    @Before
    fun setup() {
        // 1. Mock the static WorkManager class
        mockkStatic(WorkManager::class)
        every { WorkManager.getInstance(any()) } returns workManager

        // 2. Initialize ViewModel
        // Note: We don't set up repository flows here because init calls them immediately
    }

    @After
    fun tearDown() {
        unmockkStatic(WorkManager::class)
    }

    @Test
    fun alertsState_loadData_reachesSuccessState() = runTest {
        // Given
        val mockAlerts = listOf(AlertItem(id = 1, fromHour = 10, fromMinute = 0,toHour = 10, toMinute = 0 ,isAlarm = true))
        every { repository.getAllAlerts() } returns flowOf(mockAlerts)

        // When
        viewModel = AlertsViewModel(application, repository)

        // Then: Just take the current value of the StateFlow
        val result = viewModel.alertsState.value

        assertThat(result is UiState.Success, `is`(true))
        assertThat((result as UiState.Success).data.size, `is`(1))
    }

    @Test
    fun addAlert_newAlert_callsRepoAndSchedulesWork() = runTest {
        // Given
        viewModel = AlertsViewModel(application, repository)
        val alert = AlertItem(id = 1, fromHour = 10, fromMinute = 0,toHour = 10, toMinute = 0 ,isAlarm = true)
        val desc = "Rainy Day"

        // Stub the repository to return an ID
        coEvery { repository.saveAlert(alert) } returns 10L

        // When
        viewModel.addAlert(alert, desc)

        // Then
        // 1. Verify Repository was called
        coVerify(exactly = 1) { repository.saveAlert(alert) }

        // 2. Verify WorkManager enqueued the request
        verify(exactly = 1) { workManager.enqueue(any<OneTimeWorkRequest>()) }
    }

    @Test
    fun removeAlert_existingAlert_cancelsWorkAndDeleteFromRepo() = runTest {
        // Given
        viewModel = AlertsViewModel(application, repository)
        val alert = AlertItem(id = 1, fromHour = 10, fromMinute = 0,toHour = 10, toMinute = 0 ,isAlarm = true)

        // When
        viewModel.removeAlert(alert)

        // Then
        // Check if it tried to cancel work using the specific tag
        verify { workManager.cancelAllWorkByTag("alert_1") }

        // Check if it deleted from database
        coVerify { repository.deleteAlert(alert) }
    }

}