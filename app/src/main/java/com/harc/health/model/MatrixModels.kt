package com.harc.health.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Keep
@Entity(tableName = "matrix_progress")
data class MatrixProgress(
    @PrimaryKey val userId: String = "",
    val currentDay: Int = 1,
    val phase: MatrixPhase = MatrixPhase.WAKE_UP,
    val lastCompletedDate: Date? = null,
    val completedDays: List<Int> = emptyList(),
    val totalUrgesLogged: Int = 0,
    val lastRelapseDate: Date? = null,
    val hasCompletedIntake: Boolean = false,
    val lastSeenOpeningDay: Int = 0
)

enum class MatrixPhase {
    WAKE_UP,       // Days 1-7
    BREAK_THE_LOOP, // Days 8-20
    REBUILD_CONTROL, // Days 21-30
    MAINTENANCE    // Post-Day 30 stabilization
}

@Keep
data class MatrixDay(
    val dayNumber: Int,
    val title: String,
    val objective: String,
    val activities: List<MatrixActivity>,
    val phase: MatrixPhase,
    val closingReflection: String = "",
    val nextDayPreview: String = "",
    val openingBridge: String = "",
    val newThreat: String = ""
)

@Keep
data class MatrixActivity(
    val id: String,
    val title: String,
    val description: String,
    val type: MatrixActivityType,
    val interactionRequired: Boolean = true
)

enum class MatrixActivityType {
    DETECTION,
    LOGGING,
    INTERRUPTION,
    ACTION,
    ANALYSIS,
    CHALLENGE
}

@Keep
@Entity(tableName = "matrix_urge_logs")
data class UrgeLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Date = Date(),
    val intensity: Int, // 1-10
    val trigger: String,
    val environment: String,
    val outcome: String, // Resisted, Relapsed
    val durationMinutes: Int,
    val emotion: String? = null,
    val cost: String? = null,
    val lesson: String? = null,
    val futureSelfMessage: String? = null,
    val escalationStage: String? = null
)

@Keep
enum class LoopStage {
    TRIGGER,
    MENTAL_SHIFT,
    ESCALATION,
    BEHAVIOR,
    AFTERMATH
}

@Keep
data class LoopNode(
    val stage: LoopStage,
    val content: String,
    val occurrenceCount: Int = 1,
    val isInterrupted: Boolean = false
)

@Keep
data class LoopMap(
    val nodes: List<LoopNode>,
    val mostCommonTrigger: String,
    val awarenessScore: Int,
    val interventionPoint: LoopStage?
)
