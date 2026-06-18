package com.example.trip.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.trip.data.local.dao.TripDao
import com.example.trip.data.local.dao.UserDao
import com.example.trip.data.local.dao.PhotoDao
import com.example.trip.data.local.entity.TripEntity
import com.example.trip.data.local.entity.UserEntity
import com.example.trip.data.local.entity.PhotoEntity

@Database(
    entities = [UserEntity::class, TripEntity::class, PhotoEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(TripTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun tripDao(): TripDao
    abstract fun photoDao(): PhotoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS trips (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        destination TEXT NOT NULL,
                        type TEXT NOT NULL,
                        startDate INTEGER NOT NULL,
                        endDate INTEGER NOT NULL,
                        budget REAL NOT NULL,
                        description TEXT NOT NULL,
                        userId INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO trips (destination, type, startDate, endDate, budget, description, userId)
                    VALUES
                        ('Paris, França', 'LAZER', 1767225600000, 1767744000000, 8500.00, 'Viagem romântica para Paris com visita à Torre Eiffel e museus.', 1),
                        ('Nova York, EUA', 'NEGOCIOS', 1769472000000, 1769904000000, 15000.00, 'Conferência anual de tecnologia e reunião com parceiros comerciais.', 1),
                        ('Florianópolis, Brasil', 'LAZER', 1772150400000, 1772496000000, 3200.00, 'Temporada de verão nas praias do sul do Brasil.', 1)
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS photos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        tripId INTEGER NOT NULL,
                        filePath TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        FOREIGN KEY(tripId) REFERENCES trips(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_photos_tripId ON photos(tripId)")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trip_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

