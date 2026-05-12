package com.example.trip.viewmodel

import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.os.Build.VERSION
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.trip.data.repository.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

data class LocationUiState(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accuracy: Float? = null,
    val hasPermission: Boolean = false,
    val isLoading: Boolean = false,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null
)

class LocationViewModel(
    private val context: Context,
    private val repository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()

    fun onPermissionGranted() {
        _uiState.update { it.copy(hasPermission = true, isLoading = true) }
        viewModelScope.launch {

            repository.locationWithCityFlow(context)
                .catch { e -> /* tratar erro */ }
                .collect { location ->
                    _uiState.update {
                        it.copy(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            accuracy = location.accuracy,
                            city = location.city,
                            isLoading = false
                        )
                    }
                }

        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = this[APPLICATION_KEY]?.applicationContext ?: error("Context not found")
                val repository = LocationRepository(context)
                LocationViewModel(context, repository)
            }
        }
    }
}