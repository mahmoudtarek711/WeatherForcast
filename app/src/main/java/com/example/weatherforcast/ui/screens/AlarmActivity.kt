package com.example.weatherforcast.ui.screens

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.weatherforcast.R
import com.example.weatherforcast.ui.theme.*

class AlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setShowWhenLocked(true)
        setTurnScreenOn(true)

        val description = intent.getStringExtra("WEATHER_DESC")

        setContent {
            AlarmScreen(
                description = description ?: "",
                onDismiss = {
                    val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)

                    val notificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    if (notificationId != -1) {
                        notificationManager.cancel(notificationId)
                    }

                    finish()
                }
            )
        }
    }
}

@Composable
fun AlarmScreen(
    description: String,
    onDismiss: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueDark),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BluePrimary),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {

            Column(
                modifier = Modifier
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val context = LocalContext.current

                Image(
                    painter = rememberAsyncImagePainter(
                        context.packageManager.getApplicationIcon(context.packageName)
                    ),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(70.dp)
                )


                Text(
                    text = "Climora Weather Alarm",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(20.dp))


                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = description,
                    fontSize = 20.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlueAccent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                ) {

                    Text(
                        text = "Dismiss Alarm",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}