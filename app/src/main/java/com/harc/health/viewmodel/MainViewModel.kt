package com.harc.health.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.harc.health.R
import com.harc.health.logic.RecoveryEngine
import com.harc.health.logic.VitalisEngine
import com.harc.health.logic.ActionDecisionEngine
import com.harc.health.logic.SessionManager
import com.harc.health.model.HealthLog
import com.harc.health.model.User
import com.harc.health.model.VitalisData
import com.harc.health.repository.LocalRepository
import com.harc.health.repository.FirestoreRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val localRepository = LocalRepository(application)
    private val firestoreRepository = FirestoreRepository()
    private val sessionManager = SessionManager(application)
    private val auth = FirebaseAuth.getInstance()

    private val _userId = MutableStateFlow(auth.currentUser?.uid ?: "anonymous")
    val userId: StateFlow<String> = _userId.asStateFlow()

    private val _healthLog = MutableStateFlow(HealthLog())
    val healthLog: StateFlow<HealthLog> = _healthLog.asStateFlow()

    private val _recoveryScore = MutableStateFlow(0)
    val recoveryScore: StateFlow<Int> = _recoveryScore.asStateFlow()

    private val _vitalisData = MutableStateFlow(VitalisData())
    val vitalisData: StateFlow<VitalisData> = _vitalisData.asStateFlow()

    private val _adeOutput = MutableStateFlow<ActionDecisionEngine.AdeOutput?>(null)
    val adeOutput: StateFlow<ActionDecisionEngine.AdeOutput?> = _adeOutput.asStateFlow()

    private val _isPremium = MutableStateFlow(true)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _userName = MutableStateFlow("User")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    // Enhanced Stats
    private val _weeklyAlcohol = MutableStateFlow(0)
    val weeklyAlcohol = _weeklyAlcohol.asStateFlow()

    private val _weeklyCigarettes = MutableStateFlow(0)
    val weeklyCigarettes = _weeklyCigarettes.asStateFlow()

    private val _monthlyAlcohol = MutableStateFlow(0)
    val monthlyAlcohol: StateFlow<Int> = _monthlyAlcohol.asStateFlow()

    private val _monthlyCigarettes = MutableStateFlow(0)
    val monthlyCigarettes: StateFlow<Int> = _monthlyCigarettes.asStateFlow()

    private val _activeRecoveryTab = MutableStateFlow<String?>(null)
    val activeRecoveryTab = _activeRecoveryTab.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object NavigateToRecovery : UiEvent()
        object NavigateBack : UiEvent()
    }

    init {
        // Listen for Auth changes
        auth.addAuthStateListener { firebaseAuth ->
            val newUid = firebaseAuth.currentUser?.uid ?: "anonymous"
            if (_userId.value != newUid) {
                _userId.value = newUid
                refreshUserData()
            }
        }
        refreshUserData()
    }

    private fun refreshUserData() {
        updateScores()
        loadTodayLog()
        loadUserProfile()
        loadStats()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val uid = _userId.value
            localRepository.getUserProfile(uid)?.let { user ->
                _userName.value = if (user.name.isNotEmpty()) user.name else "User"
                _streak.value = user.streakDays
            }
        }
    }

    private fun loadTodayLog() {
        viewModelScope.launch {
            val uid = _userId.value
            val log = localRepository.getLogForToday(uid)
            if (log != null) {
                _healthLog.value = log
                updateScores()
            } else {
                _healthLog.value = HealthLog(userId = uid, date = getCurrentDateString())
            }
        }
    }

    private fun getCurrentDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    private fun loadStats() {
        viewModelScope.launch {
            val uid = _userId.value
            val logs = localRepository.getRecentLogs(uid, 31)
            
            val weekAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }.time
            val weeklyLogs = logs.filter { parseDate(it.date)?.after(weekAgo) == true }
            _weeklyAlcohol.value = weeklyLogs.sumOf { it.alcoholUnits }
            _weeklyCigarettes.value = weeklyLogs.sumOf { it.cigarettes }

            val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val monthlyLogs = logs.filter { log ->
                val cal = Calendar.getInstance()
                parseDate(log.date)?.let { 
                    cal.time = it
                    cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
                } ?: false
            }
            _monthlyAlcohol.value = monthlyLogs.sumOf { it.alcoholUnits }
            _monthlyCigarettes.value = monthlyLogs.sumOf { it.cigarettes }
        }
    }

    private fun parseDate(dateStr: String): Date? {
        return try {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }

    private fun saveCurrentLog() {
        viewModelScope.launch {
            val uid = _userId.value
            val log = _healthLog.value.copy(userId = uid)
            localRepository.saveLog(uid, log)
            
            // Lean Backend Sync: Only sync to Firestore if user is authenticated
            if (uid != "anonymous") {
                firestoreRepository.saveLog(uid, log)
            }
            
            loadStats()
        }
    }

    fun deleteData() {
        viewModelScope.launch {
            try {
                val uid = _userId.value
                localRepository.deleteUserData(uid)
                if (uid != "anonymous") {
                    firestoreRepository.deleteUserData(uid)
                }
                sessionManager.disableProtection()
                _healthLog.value = HealthLog(userId = uid, date = getCurrentDateString())
                updateScores()
                loadStats()
                _uiEvent.emit(UiEvent.ShowSnackbar("System recalibrated. All data purged."))
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowSnackbar("Error purging data: ${e.message}"))
            }
        }
    }

    fun setActiveRecoveryTab(tab: String?) {
        _activeRecoveryTab.value = tab
    }

    fun navigateToRecoveryWithTab(tab: String) {
        _activeRecoveryTab.value = tab
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.NavigateToRecovery)
        }
    }

    fun addDrink() {
        _healthLog.update { it.copy(alcoholUnits = it.alcoholUnits + 1) }
        updateScores()
        saveCurrentLog()
    }

    fun addCigarette() {
        _healthLog.update { it.copy(cigarettes = it.cigarettes + 1) }
        updateScores()
        saveCurrentLog()
    }

    fun addWater() {
        _healthLog.update { it.copy(hydrationMl = it.hydrationMl + 250) }
        updateScores()
        saveCurrentLog()
    }

    fun logSleep(hours: Double, quality: Int, consistency: Int, awakenings: Int) {
        _healthLog.update { 
            it.copy(
                sleepHours = hours,
                sleepQuality = quality,
                bedtimeConsistency = consistency,
                nightAwakenings = awakenings
            )
        }
        updateScores()
        saveCurrentLog()
    }

    fun logCardio(rhr: Int, activityMins: Int, sedentaryHrs: Double, hrRecovery: Int) {
        _healthLog.update {
            it.copy(
                restingHeartRate = rhr,
                activityMinutes = activityMins,
                sedentaryHours = sedentaryHrs,
                heartRateRecovery = hrRecovery
            )
        }
        updateScores()
        saveCurrentLog()
    }

    fun toggleAction(actionId: String) {
        _healthLog.update { currentLog ->
            val isCompleting = !currentLog.actionsCompleted.contains(actionId)
            
            if (isCompleting) {
                viewModelScope.launch {
                    val message = when (actionId) {
                        "h1" -> getApplication<Application>().getString(R.string.reward_h1_msg)
                        "mn1" -> getApplication<Application>().getString(R.string.reward_mn1_msg)
                        "rl1" -> getApplication<Application>().getString(R.string.reward_rl1_msg)
                        "sl1" -> getApplication<Application>().getString(R.string.reward_sl1_msg)
                        "cc2" -> getApplication<Application>().getString(R.string.reward_cc2_msg)
                        else -> getApplication<Application>().getString(R.string.reward_generic_msg)
                    }
                    _uiEvent.emit(UiEvent.ShowSnackbar(message))
                }
            }

            val newActions = if (isCompleting) {
                currentLog.actionsCompleted + actionId
            } else {
                currentLog.actionsCompleted - actionId
            }
            currentLog.copy(actionsCompleted = newActions)
        }
        updateScores()
        saveCurrentLog()
    }

    private fun updateScores() {
        _recoveryScore.value = RecoveryEngine.calculateScore(_healthLog.value)
        val vitalis = VitalisEngine.calculateVitalisData(_healthLog.value)
        _vitalisData.value = vitalis
        _adeOutput.value = ActionDecisionEngine.execute(_healthLog.value, vitalis)
        
        // Trigger smart recovery notifications
        triggerRecoveryNotifications(_healthLog.value)
    }

    private fun triggerRecoveryNotifications(log: HealthLog) {
        viewModelScope.launch {
            if (log.alcoholUnits > 8) {
                com.harc.health.logic.RecoveryNotificationManager.sendProtocolReminder(
                    getApplication(),
                    "Systemic Toxicity Alert",
                    "Critical alcohol levels. Prioritize hydration and B1 immediately."
                )
            } else if (log.cigarettes > 15) {
                com.harc.health.logic.RecoveryNotificationManager.sendProtocolReminder(
                    getApplication(),
                    "Respiratory Stress Detected",
                    "High smoking count. Execute Oxygen Loading protocol now."
                )
            } else if (log.sleepHours in 0.1..5.0) {
                 com.harc.health.logic.RecoveryNotificationManager.sendProtocolReminder(
                    getApplication(),
                    "Neural Reserve Depletion",
                    "Sleep deficit detected ($log.sleepHours hrs). Focus on Vagal Reset to manage cravings."
                )
            } else if (log.sleepHours == 0.0 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 9) {
                // Remind to log sleep if it's past 9 AM and still 0
                com.harc.health.logic.RecoveryNotificationManager.sendProtocolReminder(
                    getApplication(),
                    "Biological Data Missing",
                    "Please log last night's sleep to recalibrate your recovery plan."
                )
            }
        }
    }

    fun completeProtocol(id: String) {
        val dividend = VitalisEngine.getBiologicalGain(id)
        viewModelScope.launch {
            val message = getApplication<Application>().getString(dividend.gainRes)
            _uiEvent.emit(UiEvent.ShowSnackbar("${getApplication<Application>().getString(R.string.vitalis_neural_recalibration)}: $message"))
        }
        // Use existing toggle logic to persist the action
        toggleAction(id)
    }

    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            val uid = _userId.value
            val existing = localRepository.getUserProfile(uid) ?: User(id = uid)
            val updated = existing.copy(name = name, email = email)
            localRepository.saveUserProfile(updated)
            if (uid != "anonymous") {
                firestoreRepository.saveUserProfile(updated)
            }
            _userName.value = name
        }
    }
}
