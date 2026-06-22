package com.example.trip.ui.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri

private const val MAP_TAG = "CurrentTripMap"

/**
 * Shows the current location of the ongoing trip and opens it in the device's map app
 * using a Maps Intent (a `geo:` URI). Using an intent avoids the need for a Google Maps
 * API key or the embedded Maps SDK.
 */
@Composable
fun CurrentTripMap(
    latitude: Double,
    longitude: Double,
    title: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = "Localização atual da viagem",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = { openLocationInMaps(context, latitude, longitude, title) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Map, contentDescription = null)
                Text(
                    text = "Abrir no Google Maps",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

/**
 * Launches a map application centered on [latitude]/[longitude] with a labeled marker.
 * Prefers the Google Maps app, falls back to any geo-capable app, then the browser.
 */
private fun openLocationInMaps(
    context: Context,
    latitude: Double,
    longitude: Double,
    label: String
) {
    val encodedLabel = Uri.encode(label)
    val geoUri = "geo:$latitude,$longitude?q=$latitude,$longitude($encodedLabel)".toUri()

    val candidates = listOf(
        // Prefer the Google Maps app explicitly.
        Intent(Intent.ACTION_VIEW, geoUri).setPackage("com.google.android.apps.maps"),
        // Any app that can handle geo: URIs (shows a chooser if there are several).
        Intent(Intent.ACTION_VIEW, geoUri),
        // Last resort: open Google Maps in the browser.
        Intent(
            Intent.ACTION_VIEW,
            "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude".toUri()
        )
    )

    for (intent in candidates) {
        try {
            context.startActivity(intent)
            return
        } catch (e: ActivityNotFoundException) {
            Log.w(MAP_TAG, "No activity found for $intent, trying fallback.", e)
        }
    }
    Log.e(MAP_TAG, "Unable to open any map application for $latitude,$longitude.")
}

