package com.example.trip.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

sealed class DrawerMenuItem(
    val title: String,
    val icon: @Composable () -> Unit
) {
    data object NewTrip : DrawerMenuItem(
        title = "Nova viagem ✈️",
        icon = { Icon(Icons.Default.FlightTakeoff, contentDescription = "Nova viagem") }
    )
    
    data object MyTrips : DrawerMenuItem(
        title = "Minhas Viagens 🧳",
        icon = { Icon(Icons.Default.Luggage, contentDescription = "Minhas Viagens") }
    )
    
    data object About : DrawerMenuItem(
        title = "Sobre ℹ️",
        icon = { Icon(Icons.Default.Info, contentDescription = "Sobre") }
    )
}

@Composable
fun DrawerMenu(
    drawerState: DrawerState,
    onNewTripClick: () -> Unit,
    onMyTripsClick: () -> Unit,
    onAboutClick: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Menu 🏠",
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Nova viagem
                NavigationDrawerItem(
                    icon = DrawerMenuItem.NewTrip.icon,
                    label = { Text(DrawerMenuItem.NewTrip.title) },
                    selected = false,
                    onClick = onNewTripClick,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                
                // Minhas Viagens
                NavigationDrawerItem(
                    icon = DrawerMenuItem.MyTrips.icon,
                    label = { Text(DrawerMenuItem.MyTrips.title) },
                    selected = false,
                    onClick = onMyTripsClick,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                
                // Barra de separação
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 28.dp)
                )
                
                // Sobre
                NavigationDrawerItem(
                    icon = DrawerMenuItem.About.icon,
                    label = { Text(DrawerMenuItem.About.title) },
                    selected = false,
                    onClick = onAboutClick,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        },
        content = content
    )
}

