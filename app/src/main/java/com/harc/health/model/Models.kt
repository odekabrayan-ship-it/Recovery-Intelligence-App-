package com.harc.health.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

@Keep
@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String = "",
    val email: String = "",
    val username: String = "",
    val name: String = "",
    val bio: String = "",
    val photoUrl: String = "",
    val location: String = "",
    val gender: String = "", // Male, Female, Other, Prefer not to say
    val age: Int? = null,
    val goals: List<String> = emptyList(),
    val recoveryLevel: Int = 1, // 1–4
    val streakDays: Int = 0,
    val isAdmin: Boolean = false,
    val createdAt: Date = Date(),
    val blockedUsers: List<String> = emptyList(),
    val language: String = "en" // Default to English, e.g., "en", "tr", "es"
)

@Keep
@Entity(tableName = "health_logs")
data class HealthLog(
    @PrimaryKey val id: String = "", // userId + "_" + date
    val userId: String = "",
    val date: String = "", // ISO-8601 format or similar
    val alcoholUnits: Int = 0,
    val cigarettes: Int = 0,
    val hydrationMl: Int = 0,
    val sleepHours: Double = 0.0,
    val sleepQuality: Int = 0, // 0-100 subjective rating
    val bedtimeConsistency: Int = 0, // 0-100 percentage
    val nightAwakenings: Int = 0,
    val stressLevel: Int = 0, // 0–100
    val nutritionScore: Int = 0, // 0–100
    // Cardiovascular inputs (Module 3)
    val restingHeartRate: Int = 0,
    val activityMinutes: Int = 0,
    val sedentaryHours: Double = 0.0,
    val heartRateRecovery: Int = 0,
    // Cognitive inputs (Module 4)
    val focusDurationMinutes: Int = 0,
    val taskSwitchingCount: Int = 0,
    val perceivedFocusQuality: Int = 0, // 0-100
    val mentalFatigueLevel: Int = 0, // 0-100
    // Metabolic inputs (Module 5)
    val mealRegularityScore: Int = 0, // 0-100
    val energyLevelMorning: Int = 0, // 0-10
    val energyLevelAfternoon: Int = 0, // 0-10
    val energyLevelEvening: Int = 0, // 0-10
    val hungerStability: Int = 0, // 0-100
    val cravingsIntensity: Int = 0, // 0-100
    // Stress & Neuroendocrine inputs (Module 6)
    val workloadIntensity: Int = 0, // 0-100
    val emotionalTension: Int = 0, // 0-100
    val hrv: Int = 0, // Heart Rate Variability
    val anxietyLevel: Int = 0, // 0-100
    val actionsCompleted: List<String> = emptyList()
)

@Keep
data class RecoveryTask(
    val id: String,
    val titleRes: Int,
    val categoryRes: Int,
    val descriptionRes: Int,
    val impactScore: Int = 5
)
