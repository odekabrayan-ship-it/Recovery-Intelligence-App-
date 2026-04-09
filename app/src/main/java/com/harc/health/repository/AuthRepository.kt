package com.harc.health.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class LocalUser(
    val id: String,
    val name: String,
    val email: String
)

class AuthRepository {
    // Mocking a local user session
    private val _currentUser = MutableStateFlow<LocalUser?>(LocalUser("local_user", "Guest User", "guest@example.com"))
    val currentUser: StateFlow<LocalUser?> = _currentUser

    fun signOut() {
        _currentUser.value = null
    }

    fun signIn() {
        _currentUser.value = LocalUser("local_user", "Guest User", "guest@example.com")
    }
}
