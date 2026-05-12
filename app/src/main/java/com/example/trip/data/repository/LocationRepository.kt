package com.example.trip.data.repository

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Locale

data class LocationInfo(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val city: String?,
    val state: String?,
    val country: String?
)

class LocationRepository(private val context: Context) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        50_000L // intervalo em ms
    ).apply {
        setMinUpdateDistanceMeters(10f) // só atualiza se mover 10m
        setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
        setWaitForAccurateLocation(true)
    }.build()


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend private fun getCityFromCoordinates(
        context: Context,
        latitude: Double,
        longitude: Double
    ): String? = withContext(Dispatchers.IO) {

        val geocoder = Geocoder(context, Locale.getDefault())
        var result: String? = null
        val latch = java.util.concurrent.CountDownLatch(1)

        geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
            result = addresses.firstOrNull()?.locality
                ?: addresses.firstOrNull()?.subAdminArea
                        ?: addresses.firstOrNull()?.adminArea
            latch.countDown()
        }

        latch.await(5, java.util.concurrent.TimeUnit.SECONDS)
        result


    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun locationWithCityFlow(context: Context): Flow<LocationInfo> =
        locationFlow()
            .map { location ->
                val city = getCityFromCoordinates(
                    context,
                    location.latitude,
                    location.longitude
                )

                LocationInfo(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    accuracy = location.accuracy,
                    city = city,
                    state = null,
                    country = null
                )
            }
            .distinctUntilChangedBy { it.city } // só emite se a cidade mudou

    @RequiresPermission(anyOf = [
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ])
    fun locationFlow(): Flow<Location> = callbackFlow {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it) }
            }
        }

        fusedClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )

        awaitClose {
            fusedClient.removeLocationUpdates(callback)
        }
    }.flowOn(Dispatchers.IO)
}