package com.example.trip.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trip.data.local.entity.TripType
import com.example.trip.ui.components.MarkdownText
import com.example.trip.viewmodel.RoteiroViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoteiroScreen(
    vm: RoteiroViewModel,
    onNavigateBack: () -> Unit
) {
    val destination by vm.destination
    val type by vm.type
    val startDate by vm.startDate
    val endDate by vm.endDate
    val interests by vm.interests
    val fieldErrors by vm.fieldErrors
    val isLoading by vm.isLoading
    val errorMessage by vm.errorMessage
    val itinerary by vm.itinerary

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val startDatePickerState = rememberDatePickerState(initialSelectedDateMillis = startDate)
    val endDatePickerState = rememberDatePickerState(initialSelectedDateMillis = endDate)

    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDatePickerState.selectedDateMillis?.let { vm.onStartDateChange(it) }
                    showStartDatePicker = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endDatePickerState.selectedDateMillis?.let { vm.onEndDateChange(it) }
                    showEndDatePicker = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Roteiro com IA 🗺️") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Informe os dados da viagem e a IA criará um roteiro personalizado para você.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = destination,
                onValueChange = vm::onDestinationChange,
                label = { Text("Destino *") },
                placeholder = { Text("Ex: Paris, França") },
                isError = fieldErrors.containsKey("destination"),
                supportingText = {
                    fieldErrors["destination"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Tipo de viagem",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = type == TripType.LAZER,
                        onClick = { vm.onTypeChange(TripType.LAZER) }
                    )
                    Text(
                        text = "Lazer",
                        modifier = Modifier.padding(end = 24.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    RadioButton(
                        selected = type == TripType.NEGOCIOS,
                        onClick = { vm.onTypeChange(TripType.NEGOCIOS) }
                    )
                    Text(
                        text = "Negócios",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            OutlinedTextField(
                value = startDate?.let { dateFormatter.format(Date(it)) } ?: "",
                onValueChange = {},
                label = { Text("Data de Início *") },
                placeholder = { Text("Selecione a data") },
                readOnly = true,
                isError = fieldErrors.containsKey("startDate"),
                supportingText = {
                    fieldErrors["startDate"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Selecionar data de início")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = endDate?.let { dateFormatter.format(Date(it)) } ?: "",
                onValueChange = {},
                label = { Text("Data de Fim *") },
                placeholder = { Text("Selecione a data") },
                readOnly = true,
                isError = fieldErrors.containsKey("endDate"),
                supportingText = {
                    fieldErrors["endDate"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                trailingIcon = {
                    IconButton(onClick = { showEndDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Selecionar data de fim")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = interests,
                onValueChange = vm::onInterestsChange,
                label = { Text("Interesses *") },
                placeholder = { Text("Ex: gastronomia, museus, vida noturna, natureza...") },
                isError = fieldErrors.containsKey("interests"),
                supportingText = {
                    fieldErrors["interests"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = { vm.onGenerate() },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text("Gerando roteiro...")
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Text("Gerar Roteiro")
                    }
                }
            }

            itinerary?.let { result ->
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Seu roteiro personalizado",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MarkdownText(markdown = result)
                    }
                }
            }
        }
    }
}

