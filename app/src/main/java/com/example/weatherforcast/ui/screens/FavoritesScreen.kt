package com.example.weatherforcast.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherforcast.R
import com.example.weatherforcast.data.local.UserSettings
import com.example.weatherforcast.model.Response.ForecastResponse
import com.example.weatherforcast.model.TempUnit
import com.example.weatherforcast.ui.UiState
import com.example.weatherforcast.ui.theme.*
import com.example.weatherforcast.utils.MapSelectionSheet
import com.example.weatherforcast.utils.kelvinToCelsius
import com.example.weatherforcast.utils.kelvinToFahrenheit
import com.example.weatherforcast.viewmodels.FavoritesViewModel
import com.example.weatherforcast.viewmodels.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    settingsViewModel: SettingsViewModel,
    onCardClick: (ForecastResponse) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val favoritesState by viewModel.favoritesState.collectAsState()
    val settings by settingsViewModel.settingsState.collectAsState()
    val scope = rememberCoroutineScope()
    var showMap by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showMap = true },
                containerColor = BlueSecondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Location", tint = Color.White)
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BlueDark, BluePrimary)))
            .padding(padding)) {

            when (val uiState = favoritesState) {
                is UiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is UiState.Success -> {
                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        items(uiState.data, key = { it.city.id }) { item ->
                            val city_name_removed = stringResource(R.string.remove)+"${item.city.name}"
                            val undo_message = stringResource(R.string.undo)
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { value ->
                                    if (value == SwipeToDismissBoxValue.EndToStart) {
                                        viewModel.deleteFavorite(item)
                                        scope.launch {
                                            val result = snackbarHostState.showSnackbar(
                                                message = city_name_removed,
                                                actionLabel = undo_message,
                                                withDismissAction = true,
                                                duration = SnackbarDuration.Short
                                            )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                viewModel.addFavorite(item)
                                            }
                                        }
                                        true
                                    } else false
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = false,
                                backgroundContent = {
                                    val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) Color.Red else Color.Transparent
                                    Box(Modifier.fillMaxSize().background(color).padding(end = 20.dp), contentAlignment = Alignment.CenterEnd) {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                                    }
                                }
                            ) {
                                FavoriteCard(item = item, settings = settings, onClick = { onCardClick(item) })
                            }
                        }
                    }
                }
                is UiState.Error -> Text(stringResource(R.string.nofavfound), color = Color.White, modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    if (showMap) {
        MapSelectionSheet(
            onDismiss = { showMap = false },
            onLocationSelected = { lat, lon ->
                viewModel.addFavoriteByLocation(lat, lon, "76c0ba629d316a5c11c0ead182aefac9",settings.language.name.toLowerCase())
                showMap = false
            }
        )
    }
}

@Composable
fun FavoriteCard(item: ForecastResponse, settings: UserSettings, onClick: () -> Unit) {
    val kelvinTemp = item.list.firstOrNull()?.main?.temp ?: 0.0
    val displayTemp = when (settings.tempUnit) {
        TempUnit.C -> "${kelvinToCelsius(kelvinTemp)}°C"
        TempUnit.F -> "${kelvinToFahrenheit(kelvinTemp)}°F"
        TempUnit.K -> "${kelvinTemp.toInt()}K"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = BlueAccent.copy(alpha = 0.6f))
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(item.city.name, style = MaterialTheme.typography.headlineSmall, color = Color.White)
                Text(
                    text = item.list.firstOrNull()?.weather?.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "",
                    color = Color.LightGray
                )
            }
            Text(text = displayTemp, style = MaterialTheme.typography.displaySmall, color = Color.White)
        }
    }
}