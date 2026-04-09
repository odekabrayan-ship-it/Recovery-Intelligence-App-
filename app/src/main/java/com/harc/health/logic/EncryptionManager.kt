package com.harc.health.logic

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher

/**
 * Privacy-First Minimal Relay Chat System - Encryption Manager
 * Handles E2EE using RSA (for simplicity in this implementation, 
 * though Curve25519 is preferred for modern apps).
 */
class EncryptionManager(private val context: Context) {

    private val keyStoreAlias = "harc_chat_key_v1"
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    init {
        ensureKeysExist()
    }

    private fun ensureKeysExist() {
        if (!keyStore.containsAlias(keyStoreAlias)) {
            val kpg = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                "AndroidKeyStore"
            )
            val parameterSpec = KeyGenParameterSpec.Builder(
                keyStoreAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .build()

            kpg.initialize(parameterSpec)
            kpg.generateKeyPair()
        }
    }

    fun getPublicKey(): String {
        val publicKey = keyStore.getCertificate(keyStoreAlias).publicKey
        return Base64.encodeToString(publicKey.encoded, Base64.DEFAULT)
    }

    fun getPublicKeyObject(): PublicKey {
        return keyStore.getCertificate(keyStoreAlias).publicKey
    }

    fun getPrivateKey(): PrivateKey {
        return keyStore.getKey(keyStoreAlias, null) as PrivateKey
    }

    fun encrypt(plainText: String, recipientPublicKey: PublicKey): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, recipientPublicKey)
        val encryptedBytes = cipher.doFinal(plainText.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun decrypt(encryptedText: String): String {
        return try {
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.DECRYPT_MODE, getPrivateKey())
            val encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes)
        } catch (e: Exception) {
            "Error: Decryption failed"
        }
    }
    
    // Helper to reconstruct PublicKey from String
    fun stringToPublicKey(publicKeyString: String): PublicKey {
        val publicBytes = Base64.decode(publicKeyString, Base64.DEFAULT)
        val keyFactory = java.security.KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(java.security.spec.X509EncodedKeySpec(publicBytes))
    }
}
