package com.example.trip.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trip.data.local.entity.PhotoEntity
import com.example.trip.data.repository.PhotoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PhotosViewModel(
    private val photoRepository: PhotoRepository,
    private val tripId: Long
) : ViewModel() {

    val photos: StateFlow<List<PhotoEntity>> = photoRepository.getByTrip(tripId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addPhoto(filePath: String) {
        viewModelScope.launch {
            photoRepository.add(tripId, filePath)
        }
    }

    fun deletePhoto(photo: PhotoEntity) {
        viewModelScope.launch {
            photoRepository.delete(photo)
        }
    }

    companion object {
        fun provideFactory(
            photoRepository: PhotoRepository,
            tripId: Long
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PhotosViewModel(photoRepository, tripId) as T
                }
            }
        }
    }
}

