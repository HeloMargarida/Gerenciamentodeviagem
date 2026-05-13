package com.senac.travelapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.senac.travelapp.data.local.dao.TravelDao
import com.senac.travelapp.data.local.dao.UserDao
import com.senac.travelapp.data.local.entity.TravelEntity
import com.senac.travelapp.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, TravelEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun travelDao(): TravelDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        // ── Migration 1 → 2: cria a tabela travels ────────────────────
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS travels (
                        id         INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        destino    TEXT    NOT NULL,
                        tipo       TEXT    NOT NULL,
                        dataInicio TEXT    NOT NULL,
                        dataFim    TEXT    NOT NULL,
                        orcamento  REAL    NOT NULL,
                        userId     INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "travel_app_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
