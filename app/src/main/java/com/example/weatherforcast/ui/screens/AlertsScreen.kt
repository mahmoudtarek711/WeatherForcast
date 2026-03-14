package com.example.weatherforcast.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherforcast.R
import com.example.weatherforcast.model.AlertItem
import com.example.weatherforcast.ui.UiState
import com.example.weatherforcast.ui.components.alertscomponents.AddAlertBottomSheet
import com.example.weatherforcast.ui.theme.*
import com.example.weatherforcast.ui.viewmodels.AlertsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(viewModel: AlertsViewModel, weatherDescription: String,iconCode: String) {
    var showSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val uiState by viewModel.alertsState.collectAsStateWithLifecycle()

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
                containerColor = RainTeal,
                contentColor = TextWhite
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_alert))
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(padding)
        ) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }

                is UiState.Error -> {
                    Text(
                        text = (state as? UiState.Error)?.message ?: stringResource(R.string.erroroccured),
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Red
                    )
                }

                is UiState.Success -> {
                    val alertsList = state.data as List<AlertItem>

                    if (alertsList.isEmpty()) {
                        Text(
                            text = stringResource(R.string.noalertsfound),
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(alertsList, key = { it.id }) { alert ->
                                AlertItemRow(
                                    alert = alert,
                                    viewModel = viewModel,
                                    snackbarHostState = snackbarHostState,
                                    scope = scope,
                                    weatherDescription = weatherDescription,
                                    iconCode
                                )
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
                    viewModel.addAlert(alert, weatherDescription,iconCode)
                    showSheet = false
                }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertItemRow(
    alert: AlertItem,
    viewModel: AlertsViewModel,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    weatherDescription: String,
    iconCode: String
) {
    // UPDATED: New M3 API names
    val undo_message = stringResource(R.string.undo)
    val alert_delete_message = stringResource(R.string.alertdelete)
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                viewModel.removeAlert(alert)
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = alert_delete_message,
                        actionLabel = undo_message,
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.restoreAlert(alert, weatherDescription,iconCode)
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
        modifier = Modifier.padding(vertical = 4.dp),
        enableDismissFromStartToEnd = false, // Only swipe left to delete
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.6f)
                    else -> Color.Transparent
                }, label = "background"
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color, MaterialTheme.shapes.large)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = Color.White
                )
            }
        }
    ) {
        // This is the actual card content
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GlassWhite),
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
                Column {
                    Text(
                        text = stringResource(R.string.active_period),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextLightGrey
                    )
                    Text(
                        text = "${alert.fromHour}:${alert.fromMinute.toString().padStart(2, '0')} — ${alert.toHour}:${alert.toMinute.toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextWhite
                    )
                    Text(
                        text = if (alert.isAlarm) stringResource(R.string.alarm_mode) else stringResource(R.string.notification_mode),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextLightGrey.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}