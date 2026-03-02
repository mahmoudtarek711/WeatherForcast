package com.example.weatherforcast.ui.components.alertscomponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherforcast.model.AlertItem
import com.example.weatherforcast.ui.theme.GlassWhite
import com.example.weatherforcast.ui.theme.GlassWhiteLight
import com.example.weatherforcast.ui.theme.GreyLight
import com.example.weatherforcast.ui.theme.*
import com.example.weatherforcast.ui.theme.TextSizes
import com.example.weatherforcast.ui.theme.TextWhite
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlertBottomSheet(
    onCancel: () -> Unit,
    onAdd: (AlertItem) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    var fromHour by remember { mutableStateOf(0) }
    var fromMinute by remember { mutableStateOf(0) }
    var toHour by remember { mutableStateOf(0) }
    var toMinute by remember { mutableStateOf(0) }
    var isAlarm by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onCancel,
        containerColor = BluePrimary,
        contentColor = TextWhite,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("New Weather Alert", style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(20.dp))

            TimeInput(label = "Start Time") { h, m -> fromHour = h; fromMinute = m }
            Spacer(Modifier.height(12.dp))
            TimeInput(label = "End Time") { h, m -> toHour = h; toMinute = m }

            Spacer(Modifier.height(24.dp))

            // --- Glassy Toggle Container ---
            Surface(
                color = GlassWhiteLight,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Enable Loud Alarm", color = TextWhite)
                    Switch(
                        checked = isAlarm,
                        onCheckedChange = { isAlarm = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SunYellow,
                            checkedTrackColor = SunYellow.copy(alpha = 0.5f),
                            uncheckedThumbColor = GreyLight,
                            uncheckedTrackColor = GlassWhite
                        )
                    )
                }
            }

            // Error message moved outside the switch row for better visibility
            if (error.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(error, color = ErrorRed, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(32.dp))

            // --- Bottom Action Buttons ---
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, GlassStroke),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                ) {
                    Text("Cancel")
                }

                Spacer(Modifier.width(12.dp))

                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                    onClick = {
                        val fromTotal = fromHour * 60 + fromMinute
                        val toTotal = toHour * 60 + toMinute

                        if (toTotal <= fromTotal) {
                            error = "End time must be after start time"
                        } else {
                            onAdd(
                                AlertItem(
                                    fromHour = fromHour,
                                    fromMinute = fromMinute,
                                    toHour = toHour,
                                    toMinute = toMinute,
                                    isAlarm = isAlarm
                                )
                            )}
                    }
                ) {
                    Text("Save Alert")
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}