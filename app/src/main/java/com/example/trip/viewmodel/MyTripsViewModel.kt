package com.example.trip.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trip.data.local.entity.TripEntity
import com.example.trip.data.repository.TripRepository
import com.example.trip.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyTripsViewModel(
    private val tripRepository: TripRepository,
    private val userRepository: UserRepository,
    private val userEmail: String
) : ViewModel() {

    private val _trips = MutableStateFlow<List<TripEntity>>(emptyList())
    val trips: StateFlow<List<TripEntity>> = _trips.asStateFlow()

    var isLoading = MutableStateFlow(true)

    init {
        viewModelScope.launch {
            val user = userRepository.findByEmail(userEmail)
            if (user != null) {
                tripRepository.getByUser(user.id).collect { list ->
                    _trips.value = list
                    isLoading.value = false
                }
            } else {
                isLoading.value = false
            }
        }
    }

    fun deleteTrip(tripId: Long) {
        viewModelScope.launch {
            tripRepository.delete(tripId)
        }
    }

    companion object {
        fun provideFactory(
            tripRepository: TripRepository,
            userRepository: UserRepository,
            userEmail: String
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MyTripsViewModel(tripRepository, userRepository, userEmail) as T
                }
            }
        }
    }
}

