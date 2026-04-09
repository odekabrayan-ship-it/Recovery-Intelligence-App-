package com.harc.health.repository.local

import androidx.room.*
import com.harc.health.model.HealthLog
import com.harc.health.model.LocalMessage
import com.harc.health.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): User?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserFlow(userId: String): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)
}

@Dao
interface HealthLogDao {
    @Query("SELECT * FROM health_logs WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getLog(userId: String, date: String): HealthLog?

    @Query("SELECT * FROM health_logs WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentLogs(userId: String, limit: Int): List<HealthLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HealthLog)

    @Query("DELETE FROM health_logs WHERE userId = :userId")
    suspend fun deleteLogsForUser(userId: String)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM local_messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChat(chatId: String): Flow<List<LocalMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: LocalMessage)

    @Query("DELETE FROM local_messages WHERE chatId = :chatId")
    suspend fun deleteMessagesForChat(chatId: String)
}
