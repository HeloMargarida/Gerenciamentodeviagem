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
    version = 3,
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

        // ── Migration 2 → 3: adiciona coluna gastos ───────────────────
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE travels ADD COLUMN gastos REAL NOT NULL DEFAULT 0.0"
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}