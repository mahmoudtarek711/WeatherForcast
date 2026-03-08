package com.example.weatherforcast.utils

import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
fun MapSelectionSheet(onDismiss: () -> Unit, onLocationSelected: (Double, Double) -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.95f),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = null
    ) {
        val scope = rememberCoroutineScope()
        var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }
        var cityName by remember { mutableStateOf<String?>(null) }

        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) {
                AndroidView(factory = { context ->
                    MapView(context).apply {
                        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
                        setMultiTouchControls(true)
                        controller.setZoom(10.0)
                        controller.setCenter(GeoPoint(30.0444, 31.2357))
                        val marker = Marker(this)
                        val receiver = object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                p?.let {
                                    selectedPoint = it
                                    marker.position = it
                                    overlays.removeIf { o -> o is Marker && o != marker }; overlays.add(marker)
                                    invalidate()
                                    scope.launch(Dispatchers.IO) {
                                        val addr = Geocoder(context).getFromLocation(it.latitude, it.longitude, 1)?.firstOrNull()
                                        withContext(Dispatchers.Main) { cityName = addr?.locality ?: addr?.adminArea }
                                    }
                                }
                                return true
                            }
                            override fun longPressHelper(p: GeoPoint?) = false
                        }
                        overlays.add(MapEventsOverlay(receiver))
                    }
                }, modifier = Modifier.fillMaxSize())
            }
            Column(Modifier.padding(16.dp)) {
                Button(
                    onClick = { selectedPoint?.let { onLocationSelected(it.latitude, it.longitude) } },
                    enabled = selectedPoint != null,
                    modifier = Modifier.fillMaxWidth()
                ) { Text(cityName ?: "Select Location") }
                OutlinedButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Cancel") }
            }
        }
    }
}