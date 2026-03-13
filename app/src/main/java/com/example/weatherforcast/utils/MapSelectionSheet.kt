package com.example.weatherforcast.utils

import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.example.weatherforcast.ui.theme.*
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
fun MapSelectionSheet(onDismiss: () -> Unit, onLocationSelected: (Double, Double) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 1. DISABLE SWIPE TO CLOSE
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { false } // Prevents closing via swiping down
    )

    var searchQuery by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<android.location.Address>>(emptyList()) }
    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var cityName by remember { mutableStateOf<String?>(null) }

    val marker = remember { Marker(MapView(context)) }
    var mapViewInstance by remember { mutableStateOf<MapView?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.95f),
        sheetState = sheetState,
        dragHandle = null
    ) {
        Box(Modifier.fillMaxSize().background(BlueDark)) {
            Column(Modifier.fillMaxSize()) {
                // --- SEARCH BAR ---
                Surface(
                    modifier = Modifier.fillMaxWidth().zIndex(2f),
                    color = BluePrimary,
                    shadowElevation = 8.dp
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { query ->
                            searchQuery = query
                            if (query.length > 2) {
                                scope.launch(Dispatchers.IO) {
                                    try {
                                        // 2. INCREASED MAX RESULTS TO 5
                                        val results = Geocoder(context).getFromLocationName(query, 5)
                                        withContext(Dispatchers.Main) {
                                            suggestions = results ?: emptyList()
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) { suggestions = emptyList() }
                                    }
                                }
                            } else {
                                suggestions = emptyList()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        placeholder = { Text("Search city...", color = Color.LightGray) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = ""; suggestions = emptyList() }) {
                                    Icon(Icons.Default.Clear, null, tint = Color.White)
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = BlueSecondary,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                }

                // --- MAP SECTION ---
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectTapGestures { }
                        }
                ) {
                    AndroidView(
                        factory = { ctx ->
                            MapView(ctx).apply {
                                Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", 0))
                                setMultiTouchControls(true)
                                controller.setZoom(10.0)
                                controller.setCenter(GeoPoint(30.0444, 31.2357))
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                                overlays.add(MapEventsOverlay(object : MapEventsReceiver {
                                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                        p?.let {
                                            selectedPoint = it
                                            marker.position = it
                                            if (!overlays.contains(marker)) overlays.add(marker)
                                            invalidate()
                                            scope.launch(Dispatchers.IO) {
                                                val addr = Geocoder(ctx).getFromLocation(it.latitude, it.longitude, 1)?.firstOrNull()
                                                withContext(Dispatchers.Main) {
                                                    cityName = addr?.locality ?: addr?.featureName ?: "Selected Location"
                                                }
                                            }
                                        }
                                        return true
                                    }
                                    override fun longPressHelper(p: GeoPoint?) = false
                                }))
                                mapViewInstance = this
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // --- FOOTER ---
                Surface(color = BlueDark, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Button(
                            onClick = { selectedPoint?.let { onLocationSelected(it.latitude, it.longitude) } },
                            enabled = selectedPoint != null,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = BlueSecondary)
                        ) { Text(cityName ?: "Select Location") }
                        TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                            Text("Cancel", color = Color.White)
                        }
                    }
                }
            }

            // --- FLOATING SUGGESTIONS ---
            if (suggestions.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 85.dp)
                        .fillMaxWidth()
                        .zIndex(3f),
                    colors = CardDefaults.cardColors(containerColor = BluePrimary),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    LazyColumn(modifier = Modifier.heightIn(max = 250.dp)) {
                        items(suggestions) { address ->
                            val name = address.locality ?: address.featureName ?: "Unknown"
                            // Better formatting for the list
                            val subLabel = listOfNotNull(address.adminArea, address.countryName).joinToString(", ")

                            ListItem(
                                headlineContent = { Text(name, color = Color.White) },
                                supportingContent = { Text(subLabel, color = Color.LightGray) },
                                modifier = Modifier.clickable {
                                    val newPoint = GeoPoint(address.latitude, address.longitude)
                                    selectedPoint = newPoint
                                    cityName = name
                                    searchQuery = "$name, $subLabel"
                                    suggestions = emptyList()
                                    mapViewInstance?.let {
                                        it.controller.animateTo(newPoint)
                                        it.controller.setZoom(12.0)
                                        marker.position = newPoint
                                        if (!it.overlays.contains(marker)) it.overlays.add(marker)
                                        it.invalidate()
                                    }
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                        }
                    }
                }
            }
        }
    }
}