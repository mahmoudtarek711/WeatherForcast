package com.example.weatherforcast.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherforcast.R
import com.example.weatherforcast.model.*
import com.example.weatherforcast.ui.components.*
import com.example.weatherforcast.ui.theme.*
import com.example.weatherforcast.utils.MapSelectionSheet
import com.example.weatherforcast.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val settings by viewModel.settingsState.collectAsState()
    var showCityPicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            viewModel.updateToCurrentLocation()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BlueDark, BluePrimary, BlueSecondary)))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Text(
                    stringResource(R.string.settings),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
            }

            // Location Section
            item {
                SettingsSection(title = stringResource(R.string.location)) {
                    LocationMode.entries.forEach { mode ->
                        OptionRow(label = mode.name, selected = settings.locationMode == mode) {
                            if (mode == LocationMode.GPS) {
                                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                                } else {
                                    permissionLauncher.launch(arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ))
                                }
                            } else {
                                viewModel.updateLocationMode(mode)
                            }
                        }
                    }
                    if (settings.locationMode == LocationMode.MANUAL) {
                        OutlinedButton(
                            onClick = { showCityPicker = true },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text("${stringResource(R.string.city)}: ${settings.selectedCity}")
                        }
                    }
                }
            }

            // Temperature Unit Section
            item {
                SettingsSection(title = stringResource(R.string.temp_unit)) {
                    TempUnit.entries.forEach { unit ->
                        OptionRow(unit.name, settings.tempUnit == unit) {
                            viewModel.updateTempUnit(unit)
                        }
                    }
                }
            }

            // Wind Speed Section
            item {
                SettingsSection(title = stringResource(R.string.wind_speed)) {
                    WindUnit.entries.forEach { unit ->
                        OptionRow(unit.name, settings.windUnit == unit) {
                            viewModel.updateWindUnit(unit)
                        }
                    }
                }
            }

            // Language Section
            item {
                SettingsSection(title = stringResource(R.string.language_title)) {
                    OptionRow(
                        label = stringResource(R.string.english),
                        selected = settings.language == Language.EN,
                        onClick = { viewModel.updateLanguage(Language.EN) }
                    )
                    OptionRow(
                        label = stringResource(R.string.arabic),
                        selected = settings.language == Language.AR,
                        onClick = { viewModel.updateLanguage(Language.AR) }
                    )
                }
            }
        }

        // The Map Selection Sheet
        if (showCityPicker) {
            MapSelectionSheet(
                onDismiss = { showCityPicker = false },
                onLocationSelected = { lat, lon ->
                    viewModel.updateCityFromMap(lat, lon)
                    showCityPicker = false
                }
            )
        }
    }
}