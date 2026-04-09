package com.harc.health.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.harc.health.logic.SessionManager
import com.harc.health.model.User
import com.harc.health.repository.LocalRepository
import com.harc.health.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val localRepository = LocalRepository(application)
    private val firestoreRepository = FirestoreRepository()
    private val sessionManager = SessionManager(application)
    private val auth = FirebaseAuth.getInstance()

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isProtected = MutableStateFlow(sessionManager.isProtected())
    val isProtected: StateFlow<Boolean> = _isProtected.asStateFlow()

    init {
        // Observe Auth state to switch profiles dynamically
        auth.addAuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid ?: "anonymous"
            loadUserProfile(uid)
        }
        
        val initialUid = auth.currentUser?.uid ?: "anonymous"
        loadUserProfile(initialUid)
    }

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Try Local Cache first for speed (Smooth UX)
                var profile = localRepository.getUserProfile(userId)
                
                // 2. If authenticated, try to fetch/sync from Cloud (Reliability)
                if (userId != "anonymous") {
                    val cloudProfile = firestoreRepository.getUserProfile(userId)
                    if (cloudProfile != null) {
                        profile = cloudProfile
                        localRepository.saveUserProfile(cloudProfile)
                    }
                }

                if (profile != null) {
                    _userProfile.value = profile
                } else {
                    // Create default profile if none exists anywhere
                    val defaultUsername = "user_${userId.take(4)}"
                    val newUser = User(
                        id = userId,
                        username = defaultUsername,
                        name = if (userId == "anonymous") "Member" else "Recovery Hero",
                        language = Locale.getDefault().language,
                        email = auth.currentUser?.email ?: ""
                    )
                    _userProfile.value = newUser
                    localRepository.saveUserProfile(newUser)
                    if (userId != "anonymous") {
                        firestoreRepository.saveUserProfile(newUser)
                    }
                }
            } catch (e: Exception) {
                _error.value = "Profile Sync Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(name: String, username: String, bio: String, location: String, gender: String, age: Int?) {
        val current = _userProfile.value ?: return
        val uid = auth.currentUser?.uid ?: "anonymous"
        
        if (username.length < 3) {
            _error.value = "Username too short"
            return
        }

        val updated = current.copy(
            name = name, 
            username = username.trim().lowercase(Locale.ROOT),
            bio = bio, 
            location = location, 
            gender = gender, 
            age = age
        )
        
        _userProfile.value = updated
        viewModelScope.launch {
            try {
                localRepository.saveUserProfile(updated)
                if (uid != "anonymous") {
                    firestoreRepository.saveUserProfile(updated)
                }
            } catch (e: Exception) {
                _error.value = "Update failed: ${e.message}"
            }
        }
    }

    fun updateLanguage(languageCode: String) {
        val current = _userProfile.value ?: return
        val uid = auth.currentUser?.uid ?: "anonymous"
        
        val updated = current.copy(language = languageCode)
        
        _userProfile.value = updated
        viewModelScope.launch {
            try {
                localRepository.saveUserProfile(updated)
                if (uid != "anonymous") {
                    firestoreRepository.saveUserProfile(updated)
                }
            } catch (e: Exception) {
                _error.value = "Language update failed: ${e.message}"
            }
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val uid = auth.currentUser?.uid ?: "anonymous"
                
                // 1. Purge Cloud Data
                if (uid != "anonymous") {
                    firestoreRepository.deleteUserData(uid)
                    // Note: In a production app, you might also call auth.currentUser?.delete()
                }
                
                // 2. Purge Local Data
                localRepository.deleteUserData(uid)
                localRepository.clearAllData()
                
                // 3. Clear Session & Auth
                sessionManager.disableProtection()
                auth.signOut()
                
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Account deletion failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setPin(pin: String) {
        sessionManager.setPin(pin)
        _isProtected.value = true
    }

    fun disableProtection() {
        sessionManager.disableProtection()
        _isProtected.value = false
    }

    fun clearError() { _error.value = null }
}
