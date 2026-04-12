package com.harc.health.logic

import android.content.Context
import androidx.work.*
import com.harc.health.R
import com.harc.health.repository.LocalRepository
import kotlinx.coroutines.flow.firstOrNull
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val random = Random()
        
        // We'll use a hardcoded user ID for now as we don't have a global SessionManager to get the current user
        // In a real app, this would be fetched from a SessionManager or Auth repository
        val userId = "current_user" 
        val repository = LocalRepository(applicationContext)
        val todayLog = repository.getLogForToday(userId)

        // Clinical triggers based on behavior and time
        val (title, message, sessionId) = when {
            // Behavioral Triggers (Prioritized)
            todayLog != null && todayLog.cigarettes > 0 -> {
                Triple(
                    applicationContext.getString(R.string.notif_smoker_title),
                    applicationContext.getString(R.string.notif_smoker_msg),
                    "rl_5" // Pulmonary Clearance
                )
            }
            todayLog != null && todayLog.alcoholUnits > 2 -> {
                Triple(
                    applicationContext.getString(R.string.notif_drinker_title),
                    applicationContext.getString(R.string.notif_drinker_msg),
                    null // We don't have a specific ID yet, defaults to recovery screen
                )
            }
            todayLog != null && todayLog.stressLevel > 70 -> {
                Triple(
                    applicationContext.getString(R.string.notif_stress_title),
                    applicationContext.getString(R.string.notif_stress_msg),
                    "st_1" // Stress Reset
                )
            }

            // Time-based Physiological Triggers
            hour in 6..8 -> {
                val variants = listOf(R.string.notif_morning_msg_1, R.string.notif_morning_msg_2)
                Triple(
                    applicationContext.getString(R.string.notif_morning_title),
                    applicationContext.getString(variants[random.nextInt(variants.size)]),
                    "st_1" // Circadian Sync/Stress Reset
                )
            }
            hour in 11..13 -> {
                val variants = listOf(R.string.notif_midday_msg_1, R.string.notif_midday_msg_2)
                Triple(
                    applicationContext.getString(R.string.notif_midday_title),
                    applicationContext.getString(variants[random.nextInt(variants.size)]),
                    "cl_1" // Focus Alpha
                )
            }
            hour in 15..17 -> {
                val variants = listOf(R.string.notif_afternoon_msg_1, R.string.notif_afternoon_msg_2)
                Triple(
                    applicationContext.getString(R.string.notif_afternoon_title),
                    applicationContext.getString(variants[random.nextInt(variants.size)]),
                    "st_2" // Panic Brake/Vagal Reset
                )
            }
            hour in 20..21 -> {
                val variants = listOf(R.string.notif_evening_msg_1, R.string.notif_evening_msg_2)
                Triple(
                    applicationContext.getString(R.string.notif_evening_title),
                    applicationContext.getString(variants[random.nextInt(variants.size)]),
                    null // General Recovery
                )
            }
            hour in 22..23 || hour in 0..1 -> {
                val variants = listOf(R.string.notif_night_msg_1, R.string.notif_night_msg_2)
                Triple(
                    applicationContext.getString(R.string.notif_night_title),
                    applicationContext.getString(variants[random.nextInt(variants.size)]),
                    "sl_1" // Insomnia Relief
                )
            }
            else -> return Result.success()
        }

        RecoveryNotificationManager.sendProtocolReminder(applicationContext, title, message, sessionId)
        
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "BioDailyNotificationWork"

        fun scheduleDailyNotifications(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val repeatingRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                2, TimeUnit.HOURS 
            )
            .setConstraints(constraints)
            .setInitialDelay(15, TimeUnit.MINUTES)
            .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                repeatingRequest
            )
        }
    }
}
