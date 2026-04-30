package com.example.trip.data.repository

import com.example.trip.data.local.dao.TripDao
import com.example.trip.data.local.entity.TripEntity
import com.example.trip.data.local.entity.TripType
import kotlinx.coroutines.flow.Flow

class TripRepository(private val tripDao: TripDao) {

    sealed class SaveTripResult {
        data class Success(val tripId: Long) : SaveTripResult()
        data class Error(val message: String) : SaveTripResult()
    }

    suspend fun save(
        destination: String,
        type: TripType,
        startDate: Long,
        endDate: Long,
        budget: Double,
        description: String,
        userId: Long
    ): SaveTripResult {
        return try {
            val trip = TripEntity(
                destination = destination,
                type = type,
                startDate = startDate,
                endDate = endDate,
                budget = budget,
                description = description,
                userId = userId
            )
            val id = tripDao.insert(trip)
            SaveTripResult.Success(id)
        } catch (e: Exception) {
            SaveTripResult.Error(e.message ?: "Erro ao salvar viagem")
        }
    }

    suspend fun update(
        id: Long,
        destination: String,
        type: TripType,
        startDate: Long,
        endDate: Long,
        budget: Double,
        description: String,
        userId: Long
    ): SaveTripResult {
        return try {
            val trip = TripEntity(
                id = id,
                destination = destination,
                type = type,
                startDate = startDate,
                endDate = endDate,
                budget = budget,
                description = description,
                userId = userId
            )
            tripDao.update(trip)
            SaveTripResult.Success(id)
        } catch (e: Exception) {
            SaveTripResult.Error(e.message ?: "Erro ao atualizar viagem")
        }
    }

    suspend fun delete(id: Long) = tripDao.delete(id)

    suspend fun findById(id: Long): TripEntity? = tripDao.findById(id)
    fun getByUser(userId: Long): Flow<List<TripEntity>> = tripDao.getByUser(userId)

    fun getAll(): Flow<List<TripEntity>> = tripDao.getAll()
}

