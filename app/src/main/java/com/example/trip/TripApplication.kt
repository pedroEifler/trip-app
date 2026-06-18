package com.example.trip

import android.app.Application
import com.example.trip.data.local.AppDatabase
import com.example.trip.data.repository.PhotoRepository
import com.example.trip.data.repository.TripRepository
import com.example.trip.data.repository.UserRepository

class TripApplication : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(database.userDao())
    }

    val tripRepository: TripRepository by lazy {
        TripRepository(database.tripDao())
    }

    val photoRepository: PhotoRepository by lazy {
        PhotoRepository(database.photoDao())
    }
}

