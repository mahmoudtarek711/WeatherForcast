package com.example.weatherforcast.ui.screens

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherforcast.model.*
import com.example.weatherforcast.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {

    var tempUnit by remember { mutableStateOf(TempUnit.C) }
    var windUnit by remember { mutableStateOf(WindUnit.MS) }
    var language by remember { mutableStateOf(Language.EN) }
    var locationMode by remember { mutableStateOf(LocationMode.GPS) }
    var selectedCity by remember { mutableStateOf("Cairo") }
    var showCityPicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF2A4A62),
                        Color(0xFF355872),
                        Color(0xFF4A7A9B),
                        Color(0xFF355872),
                        Color(0xFF2A4A62)
                    )
                )
            )
            .padding(16.dp)
    ) {

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /* leave empty */ },
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Back")
                    }
                }
            }

            item {
                SettingsSection(title = "Location") {

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ToggleCard("GPS", locationMode == LocationMode.GPS) {
                            locationMode = LocationMode.GPS
                        }

                        ToggleCard("Manual", locationMode == LocationMode.MANUAL) {
                            locationMode = LocationMode.MANUAL
                        }
                    }

                    if (locationMode == LocationMode.MANUAL) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showCityPicker = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(selectedCity)
                        }
                    }
                }
            }

            item {
                SettingsSection(title = "Temperature Unit") {
                    TempUnit.values().forEach {
                        OptionRow(it.name, tempUnit == it) {
                            tempUnit = it
                        }
                    }
                }
            }

            item { SettingsSection(title = "Wind Speed") {
                WindUnit.values().forEach {
                    OptionRow(it.name, windUnit == it) {
                        windUnit = it
                    }
                }
            } }

            item {
                SettingsSection(title = "Language") {
                    Language.values().forEach {
                        OptionRow(it.name, language == it) {
                            language = it
                        }
                    }
                }
            }
        }

        if (showCityPicker) {
            ModalBottomSheet(
                onDismissRequest = { showCityPicker = false }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    listOf("Cairo", "London", "Paris", "Tokyo")
                        .forEach { city ->
                            Text(
                                text = city,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                                    .clickable {
                                        selectedCity = city
                                        showCityPicker = false
                                    }
                            )
                        }
                }
            }
        }
    }
}