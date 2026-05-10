package com.harc.health.repository

import android.content.Context
import com.harc.health.model.MatrixProgress
import com.harc.health.model.UrgeLog
import com.harc.health.repository.local.AppDatabase
import kotlinx.coroutines.flow.Flow

class MatrixRepository(context: Context) {
    private val matrixDao = AppDatabase.getDatabase(context).matrixDao()

    fun getProgress(userId: String): Flow<MatrixProgress?> = matrixDao.getProgress(userId)

    suspend fun updateProgress(progress: MatrixProgress) {
        matrixDao.updateProgress(progress)
    }

    fun getUrgeLogs(): Flow<List<UrgeLog>> = matrixDao.getUrgeLogs()

    suspend fun insertUrgeLog(log: UrgeLog) {
        matrixDao.insertUrgeLog(log)
    }

    suspend fun getLastFutureSelfMessage(): UrgeLog? = matrixDao.getLastFutureSelfMessage()

    suspend fun getProgressSync(userId: String): MatrixProgress? = matrixDao.getProgressSync(userId)
}
