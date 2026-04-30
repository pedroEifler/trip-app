package com.example.trip.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.trip.data.local.entity.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(trip: TripEntity): Long

    @Update
    suspend fun update(trip: TripEntity)

    @Query("SELECT * FROM trips WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): TripEntity?

    @Query("SELECT * FROM trips WHERE userId = :userId ORDER BY startDate DESC")
    fun getByUser(userId: Long): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips ORDER BY startDate DESC")
    fun getAll(): Flow<List<TripEntity>>

    @Query("DELETE FROM trips WHERE id = :id")
    suspend fun delete(id: Long): Int
}

