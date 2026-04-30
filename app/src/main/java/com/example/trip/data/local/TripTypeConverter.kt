package com.example.trip.data.local

import androidx.room.TypeConverter
import com.example.trip.data.local.entity.TripType

class TripTypeConverter {
    @TypeConverter
    fun fromTripType(value: TripType): String = value.name

    @TypeConverter
    fun toTripType(value: String): TripType = TripType.valueOf(value)
}

