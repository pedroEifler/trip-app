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
import com.example.trip.data.local.entity.TripEntity
import com.example.trip.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, TripEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(TripTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun tripDao(): TripDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migração de v1 para v2: cria a tabela trips e insere registros pré-cadastrados
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
                // Registros pré-cadastrados (userId = 1 — primeiro usuário)
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

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trip_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

