package com.example.trip.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trip.ui.components.CurrentTripCard
import com.example.trip.ui.components.CurrentTripMap
import com.example.trip.ui.components.DrawerMenu
import com.example.trip.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    email: String,
    vm: HomeViewModel,
    onSignOut: () -> Unit,
    onNavigateToNewTrip: () -> Unit,
    onNavigateToMyTrips: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToPhotos: (tripId: Long, destination: String) -> Unit,
    onNavigateToRoteiro: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val uiState by vm.uiState.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            @Suppress("MissingPermission")
            vm.onPermissionGranted()
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    DrawerMenu(
        drawerState = drawerState,
        onNewTripClick = {
            scope.launch { drawerState.close() }
            onNavigateToNewTrip()
        },
        onMyTripsClick = {
            scope.launch { drawerState.close() }
            onNavigateToMyTrips()
        },
        onAboutClick = {
            scope.launch { drawerState.close() }
            onNavigateToAbout()
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Trip 🏠") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToRoteiro,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = "Roteiro"
                            )
                        },
                        label = { Text("Roteiro") }
                    )
                    NavigationBarItem(
                        selected = false,
                        enabled = uiState.currentTrip != null,
                        onClick = {
                            uiState.currentTrip?.let { onNavigateToPhotos(it.id, it.destination) }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Fotos"
                            )
                        },
                        label = { Text("Fotos") }
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "Bem-vindo!")
                    Text(text = "E-mail: $email")

                    HorizontalDivider()

                    if (uiState.isLoadingLocation) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CircularProgressIndicator()
                            Text("Obtendo localização...")
                        }
                    } else if (uiState.city != null) {
                        Text(text = "📍 Cidade atual: ${uiState.city}")
                        val currentTrip = uiState.currentTrip
                        if (currentTrip != null) {
                            CurrentTripCard(trip = currentTrip, totalExpenses = uiState.totalExpenses)
                            val latitude = uiState.latitude
                            val longitude = uiState.longitude
                            if (latitude != null && longitude != null) {
                                // Opens the current trip location in Google Maps via a Maps Intent.
                                CurrentTripMap(
                                    latitude = latitude,
                                    longitude = longitude,
                                    title = currentTrip.destination
                                )
                            }
                        } else {
                            Text(
                                text = "Nenhuma viagem registrada para ${uiState.city} na data atual.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else if (!uiState.hasPermission) {
                        Text(text = "Permissão de localização não concedida.")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onSignOut,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sair")
                }
            }
        }
    }
}


