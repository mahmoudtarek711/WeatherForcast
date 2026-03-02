package com.example.weatherforcast.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.weatherforcast.model.AlertItem
import com.example.weatherforcast.ui.components.alertscomponents.AddAlertBottomSheet
import com.example.weatherforcast.ui.theme.*
import com.example.weatherforcast.ui.viewmodels.AlertsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(viewModel: AlertsViewModel) {
    var showSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Collecting StateFlow from ViewModel correctly
    val alerts by viewModel.alerts.collectAsState()

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(BlueDark, BluePrimary, BlueSecondary)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSheet = true },
                containerColor = BlueAccent,
                contentColor = TextWhite
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Alert")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(padding)
        ) {
            if (alerts.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No alerts set",
                        color = TextLightGrey,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = alerts,
                        key = { it.id } // Use the Room ID as the key for stable animations
                    ) { alert ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                    val alertToDelete = alert
                                    viewModel.removeAlert(alertToDelete)

                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Alert removed",
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            viewModel.restoreAlert(alertToDelete)
                                        }
                                    }
                                    true
                                } else {
                                    false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                    ErrorRed.copy(alpha = 0.8f)
                                } else Color.Transparent

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color, MaterialTheme.shapes.large)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.White
                                    )
                                }
                            }
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = GlassWhite,
                                shape = MaterialTheme.shapes.large,
                                border = BorderStroke(1.dp, GlassStroke)
                            ) {
                                Row(
                                    modifier = Modifier.padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(GlassWhiteLight, MaterialTheme.shapes.medium),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (alert.isAlarm) Icons.Default.Alarm else Icons.Default.Notifications,
                                            contentDescription = null,
                                            tint = RainTeal,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    Spacer(Modifier.width(16.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Active Period",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = TextLightGrey
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = "${alert.fromHour}:${alert.fromMinute.toString().padStart(2, '0')} — ${alert.toHour}:${alert.toMinute.toString().padStart(2, '0')}",
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = TextWhite
                                        )
                                        Text(
                                            text = if (alert.isAlarm) "Alarm Mode" else "Notification Mode",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextLightGrey.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showSheet) {
                AddAlertBottomSheet(
                    onCancel = { showSheet = false },
                    onAdd = { alert ->
                        viewModel.addAlert(alert)
                        showSheet = false
                    }
                )
            }
        }
    }
}