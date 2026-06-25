package com.example.trip.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trip.data.local.entity.TripType
import com.example.trip.data.repository.RoteiroRepository
import kotlinx.coroutines.launch

/**
 * Manages the UI state and presentation logic for the AI itinerary screen.
 *
 * The user informs the trip data (destination, period and interests) and the
 * ViewModel asks [RoteiroRepository] to generate a personalized itinerary
 * through the Gemini LLM.
 */
class RoteiroViewModel(
    private val roteiroRepository: RoteiroRepository
) : ViewModel() {

    var destination = mutableStateOf("")
    var type = mutableStateOf<TripType?>(null)
    var startDate = mutableStateOf<Long?>(null)
    var endDate = mutableStateOf<Long?>(null)
    var interests = mutableStateOf("")

    var fieldErrors = mutableStateOf<Map<String, String>>(emptyMap())
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    var itinerary = mutableStateOf<String?>(null)

    fun onDestinationChange(v: String) { destination.value = v }
    fun onTypeChange(v: TripType) { type.value = v }
    fun onStartDateChange(v: Long) { startDate.value = v }
    fun onEndDateChange(v: Long) { endDate.value = v }
    fun onInterestsChange(v: String) { interests.value = v }

    private fun validate(): Boolean {
        val errors = mutableMapOf<String, String>()
        if (destination.value.isBlank()) errors["destination"] = "Informe o destino"
        if (startDate.value == null) errors["startDate"] = "Informe a data de início"
        if (endDate.value == null) errors["endDate"] = "Informe a data de fim"
        if (startDate.value != null && endDate.value != null && endDate.value!! < startDate.value!!) {
            errors["endDate"] = "Data de fim deve ser após a data de início"
        }
        if (interests.value.isBlank()) errors["interests"] = "Informe seus interesses"
        fieldErrors.value = errors
        return errors.isEmpty()
    }

    fun onGenerate() {
        if (!validate()) return
        isLoading.value = true
        errorMessage.value = null
        itinerary.value = null
        viewModelScope.launch {
            val result = roteiroRepository.generateItinerary(
                destination = destination.value.trim(),
                startDate = startDate.value!!,
                endDate = endDate.value!!,
                interests = interests.value.trim(),
                tripType = type.value?.let {
                    if (it == TripType.LAZER) "Lazer" else "Negócios"
                }
            )
            when (result) {
                is RoteiroRepository.RoteiroResult.Success -> {
                    itinerary.value = result.itinerary
                    errorMessage.value = null
                }
                is RoteiroRepository.RoteiroResult.Error -> {
                    errorMessage.value = result.message
                }
            }
            isLoading.value = false
        }
    }

    companion object {
        fun provideFactory(
            roteiroRepository: RoteiroRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RoteiroViewModel(roteiroRepository) as T
                }
            }
        }
    }
}

