package com.example.weatherforcast.viewmodels

import com.example.architechturestartercode.data.movie.repository.ForcastRepository
import com.example.weatherforcast.model.Response.ForecastResponse
import com.example.weatherforcast.ui.UiState
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    // 1. Setup the Coroutine rule
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // 2. Create Mocks
    private val repository: ForcastRepository = mockk(relaxed = true)
    private lateinit var viewModel: FavoritesViewModel

    @Test
    fun loadFavorites_hasData_emitsSuccessState() = runTest {
        // Given: The repository will return a list with 1 item
        val fakeList = listOf(mockk<ForecastResponse>(relaxed = true))
        every { repository.getStoredForecasts() } returns flowOf(fakeList)

        // When: We initialize the ViewModel (which calls loadFavorites)
        viewModel = FavoritesViewModel(repository)

        // Then: Check if the state is Success and contains our list
        val currentState = viewModel.favoritesState.value
        assertThat(currentState is UiState.Success, `is`(true))
        assertThat((currentState as UiState.Success).data.size, `is`(1))
    }

    @Test
    fun deleteFavorite_clickDelete_callsRepository() = runTest {
        // Given
        viewModel = FavoritesViewModel(repository) // repo is already mocked
        val itemToDelete: ForecastResponse = mockk(relaxed = true)

        // When
        viewModel.deleteFavorite(itemToDelete)

        // Then: Verify the viewmodel asked the repository to delete
        coVerify(exactly = 1) { repository.deleteForecastItem(itemToDelete) }
    }



    @Test
    fun addFavorite_clickAdd_callsRepository() = runTest {
        // Given
        viewModel = FavoritesViewModel(repository) // repo is already mocked
        val itemToAdd: ForecastResponse = mockk(relaxed = true)

        // When
        viewModel.addFavorite(itemToAdd)

        // Then: Verify the viewmodel asked the repository to delete
        coVerify(exactly = 1) { repository.saveForecastItem(itemToAdd) }
    }
}