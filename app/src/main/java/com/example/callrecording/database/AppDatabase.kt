package com.example.callrecording.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.callrecording.database.dao.PhoneCallDAO
import com.example.callrecording.database.tables.PhoneCall

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE phone_call "
                + " ADD COLUMN caller VARCHAR")
    }
}

@Database(
    entities = [PhoneCall::class],
    version = 2,
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun phoneCallDao(): PhoneCallDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "call_record_db"
                ) .addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }
    }
}