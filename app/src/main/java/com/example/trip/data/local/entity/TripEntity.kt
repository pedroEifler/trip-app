package com.example.trip.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TripType {
    LAZER,
    NEGOCIOS
}

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val destination: String,
    val type: TripType,
    val startDate: Long,
    val endDate: Long,
    val budget: Double,
    val description: String,
    val userId: Long
)

