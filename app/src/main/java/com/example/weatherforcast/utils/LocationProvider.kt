package com.example.weatherforcast.utils

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

// LocationProvider.kt
class LocationProvider(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Pair<Double, Double>? = suspendCancellableCoroutine { cont ->
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                cont.resume(Pair(location.latitude, location.longitude))
            } else {
                cont.resume(null)
            }
        }.addOnFailureListener { cont.resume(null) }
    }
}