package com.harc.health.logic

import android.content.Context
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import com.harc.health.R
import com.harc.health.repository.LocalRepository
import com.harc.health.repository.MatrixRepository
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val sessionManager = SessionManager(applicationContext)
        if (!sessionManager.isNotificationsEnabled()) {
            return Result.success()
        }

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        if (sessionManager.isQuietHoursEnabled() && (hour >= 22 || hour < 7)) {
            return Result.success()
        }

        val random = Random()
        
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"
        val repository = LocalRepository(applicationContext)
        val matrixRepository = MatrixRepository(applicationContext)
        
        val matrixProgress = matrixRepository.getProgressSync(userId)
        val matrixDirective = matrixProgress?.let { MatrixEngine.getDirectiveForDay(it.currentDay) }

        val todayLog = repository.getLogForToday(userId)
        val recentLogs = repository.getRecentLogs(userId, 7)
        val yesterdayLog = recentLogs.find { it.date != todayLog?.date }

        // Detection of "Micro-Wins" (Comparison with History)
        val microWin = when {
            todayLog != null && yesterdayLog != null -> {
                when {
                    todayLog.cigarettes < yesterdayLog.cigarettes && todayLog.cigarettes < 5 -> {
                        Triple(
                            applicationContext.getString(R.string.notif_microwin_general_title),
                            applicationContext.getString(R.string.notif_microwin_general_msg),
                            null
                        )
                    }
                    todayLog.alcoholUnits < yesterdayLog.alcoholUnits && todayLog.alcoholUnits < 2 -> {
                        Triple(
                            applicationContext.getString(R.string.notif_microwin_general_title),
                            "Your metabolic load is lower today. Systemic recovery is accelerating. Micro-Win!",
                            null
                        )
                    }
                    todayLog.hydrationMl > yesterdayLog.hydrationMl + 500 -> {
                        Triple(
                            applicationContext.getString(R.string.notif_microwin_general_title),
                            "Hydration levels are significantly higher today. Cellular waste clearance is optimized. Micro-Win!",
                            null
                        )
                    }
                    else -> null
                }
            }
            else -> null
        }

        // Clinical triggers based on behavior and time
        val (title, message, sessionId) = when {
            // Micro-Win Detection
            microWin != null -> microWin

            // Matrix Daily Directive (Highest Priority)
            matrixDirective != null && hour in 8..10 -> {
                Triple(
                    "MATRIX DIRECTIVE: DAY ${matrixProgress.currentDay}",
                    "MANDATORY: ${matrixDirective.title}. ${matrixDirective.objective}",
                    "matrix_screen" // Hypothetical screen ID or handle in deep link
                )
            }

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

        val (finalTitle, finalMessage) = if (sessionManager.isPrivacyModeEnabled()) {
            Pair(
                applicationContext.getString(R.string.notif_privacy_title),
                applicationContext.getString(R.string.notif_privacy_msg)
            )
        } else {
            Pair(title, message)
        }

        RecoveryNotificationManager.sendProtocolReminder(applicationContext, finalTitle, finalMessage, sessionId)
        
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
