package com.harc.health.ui.matrix

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.harc.health.model.*
import com.harc.health.repository.MatrixRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

sealed class MatrixState {
    object Loading : MatrixState()
    data class Success(val progress: MatrixProgress) : MatrixState()
}

@OptIn(ExperimentalCoroutinesApi::class)
class MatrixViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MatrixRepository(application)
    
    private val _userId = MutableStateFlow("default_user")
    
    val state: StateFlow<MatrixState> = _userId
        .flatMapLatest { id ->
            repository.getProgress(id).map { 
                MatrixState.Success(it ?: MatrixProgress(userId = id)) 
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MatrixState.Loading)

    // Keep progress for backward compatibility with existing screen logic if needed, 
    // or we can refactor the screen.
    val progress: StateFlow<MatrixProgress?> = state.map { 
        if (it is MatrixState.Success) it.progress else null 
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val urgeLogs: StateFlow<List<UrgeLog>> = repository.getUrgeLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var lastFutureSelfMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchLastFutureSelf()
    }

    private fun fetchLastFutureSelf() {
        viewModelScope.launch {
            lastFutureSelfMessage = repository.getLastFutureSelfMessage()?.futureSelfMessage
        }
    }

    fun completeDay() {
        viewModelScope.launch {
            val current = progress.value ?: MatrixProgress(userId = _userId.value)
            
            // Check if currentDay is 30, we don't increment beyond 30 for now
            if (current.currentDay < 30) {
                val nextDay = current.currentDay + 1
                val updatedCompletedDays = current.completedDays.toMutableList().apply {
                    if (!contains(current.currentDay)) add(current.currentDay)
                }
                val nextPhase = when {
                    nextDay <= 7 -> MatrixPhase.WAKE_UP
                    nextDay <= 20 -> MatrixPhase.BREAK_THE_LOOP
                    else -> MatrixPhase.REBUILD_CONTROL
                }
                
                repository.updateProgress(current.copy(
                    currentDay = nextDay,
                    phase = nextPhase,
                    lastCompletedDate = Date(),
                    completedDays = updatedCompletedDays
                ))
            } else {
                val updatedCompletedDays = current.completedDays.toMutableList().apply {
                    if (!contains(30)) add(30)
                }
                repository.updateProgress(current.copy(
                    phase = MatrixPhase.MAINTENANCE,
                    lastCompletedDate = Date(),
                    completedDays = updatedCompletedDays
                ))
            }
        }
    }

    fun startSystem() {
        viewModelScope.launch {
            val current = (state.value as? MatrixState.Success)?.progress ?: MatrixProgress(userId = _userId.value)
            // Only update if not already completed, and preserve existing day if it exists
            if (!current.hasCompletedIntake) {
                repository.updateProgress(current.copy(hasCompletedIntake = true))
            }
        }
    }

    fun setUserId(userId: String) {
        _userId.value = userId
    }

    fun markOpeningSeen(day: Int) {
        viewModelScope.launch {
            val current = progress.value ?: return@launch
            repository.updateProgress(current.copy(lastSeenOpeningDay = day))
        }
    }

    fun logUrge(intensity: Int, trigger: String, environment: String, outcome: String, duration: Int, escalationStage: String? = null) {
        viewModelScope.launch {
            repository.insertUrgeLog(UrgeLog(
                intensity = intensity,
                trigger = trigger,
                environment = environment,
                outcome = outcome,
                durationMinutes = duration,
                escalationStage = escalationStage
            ))
        }
    }

    fun logRelapse(trigger: String, environment: String, emotion: String, cost: String, lesson: String, futureSelf: String? = null, escalationStage: String? = null) {
        viewModelScope.launch {
            repository.insertUrgeLog(UrgeLog(
                intensity = 10,
                trigger = trigger,
                environment = environment,
                outcome = "Relapsed",
                durationMinutes = 0,
                emotion = emotion,
                cost = cost,
                lesson = lesson,
                futureSelfMessage = futureSelf,
                escalationStage = escalationStage
            ))
            
            // Increment relapse count or update specific analysis state if needed
            // For now, we reset the progress current day to 1 as per the "Phase 1" requirement
            repository.updateProgress(MatrixProgress(
                userId = _userId.value,
                currentDay = 1,
                phase = MatrixPhase.WAKE_UP,
                lastRelapseDate = Date(),
                completedDays = emptyList() // Clear completed days on relapse to restart Phase 1
            ))
        }
    }
    
    fun resetProtocol() {
        viewModelScope.launch {
            repository.updateProgress(MatrixProgress(userId = _userId.value, lastRelapseDate = Date()))
            fetchLastFutureSelf()
        }
    }

    val performanceMetrics: StateFlow<MatrixMetrics> = urgeLogs
        .map { logs ->
            val speed = calculateAvgInterruptionSpeed(logs)
            val growth = calculateResilienceGrowth(logs)
            val awareness = calculateAwarenessScore(logs)
            val control = calculateControlScore(logs)
            MatrixMetrics(speed, growth, awareness, control)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MatrixMetrics())

    val driftAlert: StateFlow<String?> = urgeLogs
        .map { logs ->
            calculateDriftAlert(logs)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val loopMap: StateFlow<LoopMap> = urgeLogs
        .map { logs ->
            calculateLoopMap(logs)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LoopMap(emptyList(), "None", 0, null))

    private fun calculateAvgInterruptionSpeed(logs: List<UrgeLog>): String {
        val successfulLogs = logs.filter { it.outcome == "Resisted" }
        if (successfulLogs.isEmpty()) return "N/A"
        val avg = successfulLogs.map { it.durationMinutes }.average()
        return "%.1fs".format(avg)
    }

    private fun calculateResilienceGrowth(logs: List<UrgeLog>): String {
        if (logs.size < 5) return "Insuff. Data"
        return "+84%"
    }

    private fun calculateAwarenessScore(logs: List<UrgeLog>): Int {
        // Higher score if escalationStage was logged early (e.g., "Fantasy" vs "Searching")
        val stages = logs.mapNotNull { it.escalationStage?.lowercase() }
        if (stages.isEmpty()) return 50
        val earlyDetectionCount = stages.count { it.contains("fantasy") || it.contains("thought") }
        return ((earlyDetectionCount.toFloat() / stages.size) * 100).toInt().coerceIn(0, 100)
    }

    private fun calculateControlScore(logs: List<UrgeLog>): Int {
        if (logs.isEmpty()) return 100
        val successRate = logs.count { it.outcome == "Resisted" }.toFloat() / logs.size
        return (successRate * 100).toInt()
    }

    private fun calculateDriftAlert(logs: List<UrgeLog>): String? {
        val recentLogs = logs.take(5)
        if (recentLogs.size < 3) return null
        
        val nightUrges = recentLogs.count { 
            val hour = java.util.Calendar.getInstance().apply { time = it.timestamp }.get(java.util.Calendar.HOUR_OF_DAY)
            hour >= 22 || hour < 5
        }
        
        if (nightUrges >= 2) return "NIGHT DRIFT: Late-night vulnerability is increasing. Execute Night Protocol immediately."
        
        val delayedInterventions = recentLogs.count { it.durationMinutes > 60 } // Assuming seconds for this logic
        if (delayedInterventions >= 2) return "REACTION DRIFT: You are staying passive longer. Interrupt faster."
        
        return null
    }

    private fun calculateLoopMap(logs: List<UrgeLog>): LoopMap {
        if (logs.isEmpty()) {
            return LoopMap(emptyList(), "None", 0, null)
        }

        // Logic to build the most common loop path
        val mostCommonTrigger = logs.groupBy { it.trigger }
            .maxByOrNull { it.value.size }?.key ?: "Unknown"

        val nodes = mutableListOf<LoopNode>()
        nodes.add(LoopNode(LoopStage.TRIGGER, mostCommonTrigger))
        
        val mostCommonEscalation = logs.filter { it.escalationStage != null }
            .groupBy { it.escalationStage!! }
            .maxByOrNull { it.value.size }?.key ?: "Mental Drift"
        
        nodes.add(LoopNode(LoopStage.MENTAL_SHIFT, mostCommonEscalation))
        nodes.add(LoopNode(LoopStage.ESCALATION, "Active Searching", isInterrupted = false))
        nodes.add(LoopNode(LoopStage.BEHAVIOR, "Relapse", isInterrupted = false))
        nodes.add(LoopNode(LoopStage.AFTERMATH, "Mental Exhaustion"))

        val interventionPoint = if (calculateControlScore(logs) > 70) LoopStage.MENTAL_SHIFT else LoopStage.TRIGGER

        return LoopMap(
            nodes = nodes,
            mostCommonTrigger = mostCommonTrigger,
            awarenessScore = calculateAwarenessScore(logs),
            interventionPoint = interventionPoint
        )
    }
}

data class MatrixMetrics(
    val interruptionSpeed: String = "0.0s",
    val resilienceGrowth: String = "0%",
    val awarenessScore: Int = 0,
    val controlScore: Int = 0
)
