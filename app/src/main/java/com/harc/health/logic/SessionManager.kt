package com.harc.health.logic

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.util.UUID

class SessionManager(context: Context) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    
    private val sharedPreferences = EncryptedSharedPreferences.create(
        "harc_health_secure_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_IS_PROTECTED = "is_protected"
    }

    init {
        if (getDeviceId() == null) {
            val newId = UUID.randomUUID().toString()
            sharedPreferences.edit().putString(KEY_DEVICE_ID, newId).apply()
        }
    }

    fun getDeviceId(): String? {
        return sharedPreferences.getString(KEY_DEVICE_ID, null)
    }

    fun setPin(pin: String) {
        sharedPreferences.edit().putString(KEY_PIN_HASH, pin).apply()
        sharedPreferences.edit().putBoolean(KEY_IS_PROTECTED, true).apply()
    }

    fun verifyPin(pin: String): Boolean {
        val storedPin = sharedPreferences.getString(KEY_PIN_HASH, null)
        return storedPin == pin
    }

    fun isProtected(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_PROTECTED, false)
    }

    fun disableProtection() {
        sharedPreferences.edit()
            .remove(KEY_PIN_HASH)
            .putBoolean(KEY_IS_PROTECTED, false)
            .apply()
    }

    fun resetSession() {
        sharedPreferences.edit().clear().apply()
    }
}
