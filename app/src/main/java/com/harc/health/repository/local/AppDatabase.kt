package com.harc.health.repository.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.harc.health.model.HealthLog
import com.harc.health.model.LocalMessage
import com.harc.health.model.MatrixProgress
import com.harc.health.model.UrgeLog
import com.harc.health.model.User

@Database(entities = [User::class, HealthLog::class, LocalMessage::class, MatrixProgress::class, UrgeLog::class], version = 8, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun healthLogDao(): HealthLogDao
    abstract fun messageDao(): MessageDao
    abstract fun matrixDao(): MatrixDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "harc_health_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
