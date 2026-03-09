package com.example.weatherforcast.ui.screens

import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.weatherforcast.R
import com.example.weatherforcast.model.*
import com.example.weatherforcast.ui.components.*
import com.example.weatherforcast.ui.theme.*
import com.example.weatherforcast.viewmodels.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

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
            item { Text(stringResource(R.string.settings), style = MaterialTheme.typography.headlineMedium, color = TextWhite) }

            // Location Section
            item {
                SettingsSection(title = stringResource(R.string.location)) {
                    LocationMode.entries.forEach { mode ->
                        OptionRow(label = mode.name, selected = settings.locationMode == mode) {
                            viewModel.updateLocationMode(mode)
                        }
                    }
                    if (settings.locationMode == LocationMode.MANUAL) {
                        OutlinedButton(onClick = { showCityPicker = true }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            Text("${stringResource(R.string.city)}: ${settings.selectedCity}", color = TextWhite)
                        }
                    }
                }
            }
            // Temp, Wind, Language sections...
            item { SettingsSection(title = stringResource(R.string.temp_unit)) { TempUnit.entries.forEach { u -> OptionRow(u.name, settings.tempUnit == u) { viewModel.updateTempUnit(u) } } } }
            item { SettingsSection(title = stringResource(R.string.wind_speed)) { WindUnit.entries.forEach { u -> OptionRow(u.name, settings.windUnit == u) { viewModel.updateWindUnit(u) } } } }
            item {
                Text(stringResource(R.string.language_title), style = MaterialTheme.typography.titleMedium, color = Color.White)

                // English Option
                OptionRow(
                    label = stringResource(R.string.english),
                    selected = settings.language == Language.EN,
                    onClick = { viewModel.updateLanguage(Language.EN) }
                )

                // Arabic Option
                OptionRow(
                    label = stringResource(R.string.arabic), // Display name in Arabic
                    selected = settings.language == Language.AR,
                    onClick = { viewModel.updateLanguage(Language.AR) }
                )
            }

            }
        }

        // --- OSMDroid City Picker ---
        if (showCityPicker) {
            ModalBottomSheet(
                onDismissRequest = { showCityPicker = false },
                modifier = Modifier.fillMaxHeight(0.95f),
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                dragHandle = null
            ) {
                val ctx = LocalContext.current
                val scope = rememberCoroutineScope()
                var selectedCityName by remember { mutableStateOf<String?>(null) }

                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        AndroidView(
                            factory = { context ->
                                Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
                                MapView(context).apply {
                                    setMultiTouchControls(true)
                                    controller.setZoom(10.0)
                                    controller.setCenter(GeoPoint(30.0444, 31.2357))

                                    val marker = Marker(this)

                                    val receiver = object : MapEventsReceiver {
                                        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                            p?.let {
                                                marker.position = it
                                                overlays.removeIf { o -> o is Marker && o != marker }
                                                overlays.add(marker)
                                                invalidate()

                                                scope.launch(Dispatchers.IO) {
                                                    val geocoder = Geocoder(context)
                                                    val addr = try { geocoder.getFromLocation(it.latitude, it.longitude, 1)?.firstOrNull() } catch (e: Exception) { null }
                                                    withContext(Dispatchers.Main) { selectedCityName = addr?.locality ?: addr?.adminArea }
                                                }
                                            }
                                            return true
                                        }
                                        override fun longPressHelper(p: GeoPoint?) = false
                                    }
                                    overlays.add(MapEventsOverlay(receiver))
                                }
                            },
                            modifier = Modifier.fillMaxSize(),
                            onRelease = { it.onDetach() }
                        )
                    }

                    // Bottom Buttons
                    Column(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = {
                                selectedCityName?.let { viewModel.updateCity(it); viewModel.updateLocationMode(LocationMode.MANUAL) }
                                showCityPicker = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(selectedCityName ?: stringResource(R.string.tap_map_to_select)) }

                        OutlinedButton(onClick = { showCityPicker = false }, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                }
            }
        }
    }
