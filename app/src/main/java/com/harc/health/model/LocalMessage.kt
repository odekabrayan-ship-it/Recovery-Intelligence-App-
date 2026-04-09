package com.harc.health.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "local_messages")
data class LocalMessage(
    @PrimaryKey val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val content: String, // Stored as plaintext locally (or we can add another layer of AES if needed)
    val timestamp: Date,
    val isMe: Boolean,
    val isEncrypted: Boolean = false // Flag if it was originally E2EE
)
