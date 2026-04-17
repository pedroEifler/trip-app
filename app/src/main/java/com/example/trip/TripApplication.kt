package com.example.trip

import android.app.Application
import com.example.trip.data.local.AppDatabase
import com.example.trip.data.repository.UserRepository

class TripApplication : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(database.userDao())
    }
}

