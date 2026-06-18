package com.example.trip.viewmodel

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trip.data.local.entity.TripEntity
import com.example.trip.data.repository.LocationRepository
import com.example.trip.data.repository.TripRepository
import com.example.trip.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val email: String = "",
    val city: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val hasPermission: Boolean = false,
    val isLoadingLocation: Boolean = false,
    val currentTrip: TripEntity? = null,
    val totalExpenses: Double = 0.0,
    val locationError: String? = null
)

class HomeViewModel(
    private val email: String,
    private val appContext: Context,
    private val locationRepository: LocationRepository,
    private val tripRepository: TripRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(email = email))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun onPermissionGranted() {
        _uiState.update { it.copy(hasPermission = true, isLoadingLocation = true) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            startLocationFlow()
        } else {
            _uiState.update { it.copy(isLoadingLocation = false, locationError = "Localização requer Android 13+") }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startLocationFlow() {
        viewModelScope.launch {
            locationRepository.locationWithCityFlow(appContext)
                .catch { _uiState.update { s -> s.copy(isLoadingLocation = false, locationError = "Erro ao obter localização") } }
                .collect { locationInfo ->
                    val city = locationInfo.city
                    _uiState.update {
                        it.copy(
                            city = city,
                            latitude = locationInfo.latitude,
                            longitude = locationInfo.longitude,
                            isLoadingLocation = false
                        )
                    }
                    if (city != null) {
                        searchTripForCity(city)
                    }
                }
        }
    }

    private suspend fun searchTripForCity(city: String) {
        val user = userRepository.findByEmail(email) ?: return
        val trip = tripRepository.findCurrentTripByCity(user.id, city)
        _uiState.update { it.copy(currentTrip = trip, totalExpenses = 0.0) }
    }

    companion object {
        fun provideFactory(
            email: String,
            context: Context,
            tripRepository: TripRepository,
            userRepository: UserRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(
                        email = email,
                        appContext = context.applicationContext,
                        locationRepository = LocationRepository(context.applicationContext),
                        tripRepository = tripRepository,
                        userRepository = userRepository
                    ) as T
                }
            }
        }
    }
}
