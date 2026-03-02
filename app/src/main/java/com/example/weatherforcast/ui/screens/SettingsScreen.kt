package com.example.weatherforcast.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.weatherforcast.model.*
import com.example.weatherforcast.ui.components.*
import com.example.weatherforcast.ui.theme.*
import com.example.weatherforcast.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val settings by viewModel.settingsState.collectAsState()
    var showCityPicker by remember { mutableStateOf(false) }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(BlueDark, BluePrimary, BlueSecondary)
    )

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Text("Settings", style = MaterialTheme.typography.headlineMedium, color = TextWhite)
            }

            // Location Settings
            item {
                SettingsSection(title = "Location") {
                    LocationMode.values().forEach { mode ->
                        OptionRow(mode.name, settings.locationMode == mode) {
                            viewModel.updateLocationMode(mode)
                        }
                    }
                    if (settings.locationMode == LocationMode.MANUAL) {
                        OutlinedButton(
                            onClick = { showCityPicker = true },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Text("City: ${settings.selectedCity}", color = TextWhite)
                        }
                    }
                }
            }

            // Temperature Unit
            item {
                SettingsSection(title = "Temperature Unit") {
                    TempUnit.values().forEach { unit ->
                        OptionRow(unit.name, settings.tempUnit == unit) {
                            viewModel.updateTempUnit(unit)
                        }
                    }
                }
            }

            // Wind Speed Unit
            item {
                SettingsSection(title = "Wind Speed") {
                    WindUnit.values().forEach { unit ->
                        OptionRow(unit.name, settings.windUnit == unit) {
                            viewModel.updateWindUnit(unit)
                        }
                    }
                }
            }

            // Language
            item {
                SettingsSection(title = "Language") {
                    Language.values().forEach { lang ->
                        OptionRow(lang.name, settings.language == lang) {
                            viewModel.updateLanguage(lang)
                        }
                    }
                }
            }
        }

        if (showCityPicker) {
            ModalBottomSheet(onDismissRequest = { showCityPicker = false }) {
                Column(modifier = Modifier.padding(16.dp)) {
                    listOf("Cairo", "London", "Paris", "Tokyo").forEach { city ->
                        Text(
                            text = city,
                            modifier = Modifier.fillMaxWidth().padding(12.dp).clickable {
                                viewModel.updateCity(city)
                                showCityPicker = false
                            }
                        )
                    }
                }
            }
        }
    }
}