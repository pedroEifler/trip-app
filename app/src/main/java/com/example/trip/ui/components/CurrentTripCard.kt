package com.example.trip.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trip.data.local.entity.TripEntity
import com.example.trip.data.local.entity.TripType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CurrentTripCard(trip: TripEntity, totalExpenses: Double) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Viagem atual", style = MaterialTheme.typography.titleMedium)
            HorizontalDivider()
            Text("Destino: ${trip.destination}")
            Text("Início: ${dateFormat.format(Date(trip.startDate))}")
            Text("Fim: ${dateFormat.format(Date(trip.endDate))}")
            Text("Tipo: ${if (trip.type == TripType.LAZER) "Lazer" else "Negócios"}")
            Text("Orçamento: ${currencyFormat.format(trip.budget)}")
            Text("Total de gastos: ${currencyFormat.format(totalExpenses)}")
        }
    }
}

