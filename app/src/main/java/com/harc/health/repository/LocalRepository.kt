package com.harc.health.repository

import android.content.Context
import com.harc.health.model.HealthLog
import com.harc.health.model.LocalMessage
import com.harc.health.model.User
import com.harc.health.repository.local.AppDatabase
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class LocalRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val userDao = database.userDao()
    private val healthLogDao = database.healthLogDao()
    private val messageDao = database.messageDao()

    suspend fun saveLog(userId: String, log: HealthLog) {
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        healthLogDao.insertLog(log.copy(userId = userId, date = dateStr, id = "${userId}_$dateStr"))
    }

    suspend fun getLogForToday(userId: String): HealthLog? {
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return healthLogDao.getLog(userId, dateStr)
    }

    suspend fun getRecentLogs(userId: String, limit: Int = 7): List<HealthLog> {
        return healthLogDao.getRecentLogs(userId, limit)
    }

    suspend fun getUserProfile(userId: String): User? {
        return userDao.getUserById(userId)
    }

    fun getUserProfileFlow(userId: String): Flow<User?> {
        return userDao.getUserFlow(userId)
    }

    suspend fun saveUserProfile(user: User) {
        userDao.insertUser(user)
    }

    suspend fun deleteUserData(userId: String) {
        userDao.deleteUser(userId)
        healthLogDao.deleteLogsForUser(userId)
    }

    suspend fun clearAllData() {
        database.clearAllTables()
    }

    fun getLocalMessagesFlow(chatId: String): Flow<List<LocalMessage>> {
        return messageDao.getMessagesForChat(chatId)
    }

    suspend fun saveLocalMessage(message: LocalMessage) {
        messageDao.insertMessage(message)
    }
}
