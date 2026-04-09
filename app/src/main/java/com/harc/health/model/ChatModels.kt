package com.harc.health.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class Chat(
    val id: String = "",
    val participants: List<String> = emptyList(),
    val participantNames: Map<String, String> = emptyMap(),
    val participantKeys: Map<String, String> = emptyMap(), // Store public keys of participants
    val isGroup: Boolean = false,
    val isOfficial: Boolean = false,
    val groupName: String? = null,
    val groupTopic: String? = null,
    val groupDescription: String? = null,
    val updatedAt: Timestamp? = null,
    val creatorId: String? = null,
    val lastMessage: String? = null,
    val lastMessageTimestamp: Timestamp? = null
)

@Keep
data class Message(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val content: String = "", // This will be the encrypted payload
    val type: String = "text",
    val replyTo: String? = null,
    val timestamp: Timestamp? = null,
    val likes: List<String> = emptyList(),
    val commentCount: Int = 0,
    val isEncrypted: Boolean = true
)

@Keep
data class Comment(
    val id: String = "",
    val postId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val content: String = "",
    val timestamp: Timestamp? = null,
    val likes: List<String> = emptyList()
)
