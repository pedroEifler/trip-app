package com.example.trip.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trip.data.local.entity.TripType
import com.example.trip.data.repository.TripRepository
import com.example.trip.data.repository.UserRepository
import kotlinx.coroutines.launch

class NewTripViewModel(
    private val tripRepository: TripRepository,
    private val userRepository: UserRepository,
    private val userEmail: String,
    private val editTripId: Long? = null
) : ViewModel() {

    var destination = mutableStateOf("")
    var type = mutableStateOf<TripType?>(null)
    var startDate = mutableStateOf<Long?>(null)
    var endDate = mutableStateOf<Long?>(null)
    var budget = mutableStateOf("")
    var description = mutableStateOf("")

    var fieldErrors = mutableStateOf<Map<String, String>>(emptyMap())
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    val isEditMode: Boolean get() = editTripId != null

    init {
        loadTrip()
    }

    fun loadTrip() {
        if (editTripId != null) {
            viewModelScope.launch {
                val trip = tripRepository.findById(editTripId)
                if (trip != null) {
                    destination.value = trip.destination
                    type.value = trip.type
                    startDate.value = trip.startDate
                    endDate.value = trip.endDate
                    budget.value = trip.budget.toString()
                    description.value = trip.description
                }
            }
        }
    }

    fun onDestinationChange(v: String) { destination.value = v }
    fun onTypeChange(v: TripType) { type.value = v }
    fun onStartDateChange(v: Long) { startDate.value = v }
    fun onEndDateChange(v: Long) { endDate.value = v }
    fun onBudgetChange(v: String) { budget.value = v }
    fun onDescriptionChange(v: String) { description.value = v }

    private fun validate(): Boolean {
        val errors = mutableMapOf<String, String>()
        if (destination.value.isBlank()) errors["destination"] = "Informe o destino"
        if (type.value == null) errors["type"] = "Selecione o tipo de viagem"
        if (startDate.value == null) errors["startDate"] = "Informe a data de início"
        if (endDate.value == null) errors["endDate"] = "Informe a data de fim"
        if (startDate.value != null && endDate.value != null && endDate.value!! < startDate.value!!) {
            errors["endDate"] = "Data de fim deve ser após a data de início"
        }
        if (budget.value.isBlank()) {
            errors["budget"] = "Informe o orçamento"
        } else if (budget.value.toDoubleOrNull() == null || budget.value.toDouble() < 0) {
            errors["budget"] = "Orçamento inválido"
        }
        if (description.value.isBlank()) errors["description"] = "Informe a descrição"
        fieldErrors.value = errors
        return errors.isEmpty()
    }

    fun onSave(onSuccess: () -> Unit) {
        if (!validate()) return
        isLoading.value = true
        viewModelScope.launch {
            val user = userRepository.findByEmail(userEmail)
            if (user == null) {
                errorMessage.value = "Usuário não encontrado"
                isLoading.value = false
                return@launch
            }

            val result = if (isEditMode) {
                tripRepository.update(
                    id = editTripId!!,
                    destination = destination.value.trim(),
                    type = type.value!!,
                    startDate = startDate.value!!,
                    endDate = endDate.value!!,
                    budget = budget.value.toDouble(),
                    description = description.value.trim(),
                    userId = user.id
                )
            } else {
                tripRepository.save(
                    destination = destination.value.trim(),
                    type = type.value!!,
                    startDate = startDate.value!!,
                    endDate = endDate.value!!,
                    budget = budget.value.toDouble(),
                    description = description.value.trim(),
                    userId = user.id
                )
            }

            when (result) {
                is TripRepository.SaveTripResult.Success -> {
                    errorMessage.value = null
                    onSuccess()
                }
                is TripRepository.SaveTripResult.Error -> {
                    errorMessage.value = result.message
                }
            }
            isLoading.value = false
        }
    }

    companion object {
        fun provideFactory(
            tripRepository: TripRepository,
            userRepository: UserRepository,
            userEmail: String,
            editTripId: Long? = null
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NewTripViewModel(tripRepository, userRepository, userEmail, editTripId) as T
                }
            }
        }
    }
}

