package com.example.trip.data.repository

import com.example.trip.data.local.dao.PhotoDao
import com.example.trip.data.local.entity.PhotoEntity
import kotlinx.coroutines.flow.Flow

class PhotoRepository(private val photoDao: PhotoDao) {

    fun getByTrip(tripId: Long): Flow<List<PhotoEntity>> = photoDao.getByTrip(tripId)

    suspend fun add(tripId: Long, filePath: String): Long =
        photoDao.insert(PhotoEntity(tripId = tripId, filePath = filePath))

    suspend fun delete(photo: PhotoEntity) = photoDao.delete(photo)
}

