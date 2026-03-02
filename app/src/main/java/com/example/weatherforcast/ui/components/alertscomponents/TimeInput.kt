package com.example.weatherforcast.ui.components.alertscomponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeInput(
    label: String,
    onTimeSelected: (Int, Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val state = rememberTimePickerState()
    var selectedTimeText by remember { mutableStateOf("Select Time") }

    Column {
        OutlinedButton(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("$label: $selectedTimeText")
        }

        if (showDialog) {
            DatePickerDialog( // Wrapper for the dialog look
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        onTimeSelected(state.hour, state.minute)
                        selectedTimeText = "${state.hour}:${state.minute.toString().padStart(2, '0')}"
                        showDialog = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                }
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    TimePicker(state = state)
                }
            }
        }
    }
}