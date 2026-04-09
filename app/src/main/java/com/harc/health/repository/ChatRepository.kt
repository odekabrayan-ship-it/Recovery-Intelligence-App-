package com.harc.health.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.harc.health.model.Chat
import com.harc.health.model.Message
import com.harc.health.model.Comment
import com.harc.health.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Locale

/**
 * Privacy-First Minimal Relay Chat Repository
 * In this architecture, Firebase acts as the relay.
 * Messages are E2EE - the server never sees plaintext.
 */
class ChatRepository {
    private val db = FirebaseFirestore.getInstance()
    private val chatsCollection = db.collection("chats")
    private val messagesCollection = db.collection("messages")
    private val feedCollection = db.collection("feed")
    private val commentsCollection = db.collection("comments")
    private val usersCollection = db.collection("users")
    private val reportsCollection = db.collection("reports")

    companion object {
        const val OFFICIAL_CHAT_ID = "harc_announcements"
    }

    suspend fun saveUserProfile(user: User, publicKey: String? = null) {
        try {
            val profile = mutableMapOf(
                "id" to user.id,
                "name" to user.name,
                "username" to user.username,
                "username_lowercase" to user.username.lowercase(Locale.ROOT),
                "email" to user.email.lowercase(Locale.ROOT),
                "photoUrl" to user.photoUrl
            )
            if (publicKey != null) {
                profile["publicKey"] = publicKey
            }
            usersCollection.document(user.id).set(profile, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error syncing chat profile", e)
        }
    }

    suspend fun findUserByQuery(query: String): User? {
        if (query.isBlank()) return null
        val normalizedQuery = query.trim().lowercase(Locale.ROOT)
        
        return try {
            // 1. Try searching by lowercase username
            val usernameMatch = usersCollection.whereEqualTo("username_lowercase", normalizedQuery).limit(1).get().await()
            if (!usernameMatch.isEmpty) {
                return mapDocToUser(usernameMatch.documents[0])
            }
            
            // 2. Try searching by email
            val emailMatch = usersCollection.whereEqualTo("email", normalizedQuery).limit(1).get().await()
            if (!emailMatch.isEmpty) {
                return mapDocToUser(emailMatch.documents[0])
            }

            // 3. Try exact display name match (optional, but helpful)
            val nameMatch = usersCollection.whereEqualTo("name", query.trim()).limit(1).get().await()
            if (!nameMatch.isEmpty) {
                return mapDocToUser(nameMatch.documents[0])
            }
            
            null
        } catch (e: Exception) {
            Log.e("ChatRepository", "Search failed", e)
            null
        }
    }

    private fun mapDocToUser(doc: com.google.firebase.firestore.DocumentSnapshot): User {
        return User(
            id = doc.id,
            name = doc.get("name")?.toString() ?: "",
            username = doc.get("username")?.toString() ?: "",
            email = doc.get("email")?.toString() ?: "",
            photoUrl = doc.get("photoUrl")?.toString() ?: ""
        )
    }

    suspend fun getUserPublicKey(userId: String): String? {
        return try {
            val doc = usersCollection.document(userId).get().await()
            doc.getString("publicKey")
        } catch (e: Exception) {
            null
        }
    }

    fun getChats(userId: String): Flow<List<Chat>> = callbackFlow {
        val subscription = chatsCollection
            .whereArrayContains("participants", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val chats = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Chat::class.java)?.copy(id = doc.id)
                    }
                    trySend(chats)
                }
            }
        awaitClose { subscription.remove() }
    }

    fun getMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val subscription = messagesCollection
            .whereEqualTo("chatId", chatId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val messages = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Message::class.java)?.copy(id = doc.id)
                    }
                    trySend(messages)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun sendEncryptedMessage(chatId: String, senderId: String, senderName: String, encryptedContent: String) {
        val messageId = messagesCollection.document().id
        val message = Message(
            id = messageId,
            chatId = chatId,
            senderId = senderId,
            senderName = senderName,
            content = encryptedContent,
            isEncrypted = true,
            timestamp = Timestamp.now()
        )
        
        db.runBatch { batch ->
            batch.set(messagesCollection.document(messageId), message)
            batch.update(chatsCollection.document(chatId), 
                "lastMessage", "[Encrypted Message]", 
                "lastMessageTimestamp", Timestamp.now(),
                "updatedAt", Timestamp.now()
            )
        }.await()
    }

    suspend fun deleteMessage(messageId: String) {
        try {
            messagesCollection.document(messageId).delete().await()
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error deleting message", e)
        }
    }

    fun getFeedMessages(): Flow<List<Message>> = callbackFlow {
        val subscription = feedCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val feed = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Message::class.java)?.copy(id = doc.id)
                    }
                    trySend(feed)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun sendFeedMessage(senderId: String, senderName: String, content: String) {
        val messageId = feedCollection.document().id
        val message = Message(
            id = messageId,
            chatId = "global_feed",
            senderId = senderId,
            senderName = senderName,
            content = content,
            isEncrypted = false,
            timestamp = Timestamp.now()
        )
        feedCollection.document(messageId).set(message).await()
    }

    suspend fun toggleLike(collectionPath: String, messageId: String, userId: String) {
        val docRef = db.collection(collectionPath).document(messageId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val likes = snapshot.get("likes") as? List<*> ?: emptyList<String>()
            val likesList = likes.filterIsInstance<String>()
            
            if (likesList.contains(userId)) {
                transaction.update(docRef, "likes", FieldValue.arrayRemove(userId))
            } else {
                transaction.update(docRef, "likes", FieldValue.arrayUnion(userId))
            }
        }.await()
    }

    fun getComments(postId: String): Flow<List<Comment>> = callbackFlow {
        val subscription = commentsCollection
            .whereEqualTo("postId", postId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val comments = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Comment::class.java)?.copy(id = doc.id)
                    }
                    trySend(comments)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addComment(postId: String, collectionPath: String, senderId: String, senderName: String, content: String) {
        val commentId = commentsCollection.document().id
        val comment = Comment(
            id = commentId,
            postId = postId,
            senderId = senderId,
            senderName = senderName,
            content = content,
            timestamp = Timestamp.now()
        )
        
        db.runBatch { batch ->
            batch.set(commentsCollection.document(commentId), comment)
            batch.update(db.collection(collectionPath).document(postId), "commentCount", FieldValue.increment(1))
        }.await()
    }

    suspend fun getOrCreatePrivateChat(myId: String, myName: String, myPublicKey: String, otherId: String, otherName: String, otherPublicKey: String): String {
        val participants = listOf(myId, otherId).sorted()
        val existing = chatsCollection
            .whereEqualTo("isGroup", false)
            .whereEqualTo("participants", participants)
            .limit(1)
            .get()
            .await()

        if (!existing.isEmpty) {
            return existing.documents[0].id
        }

        val chatId = chatsCollection.document().id
        val chat = Chat(
            id = chatId,
            participants = participants,
            participantNames = mapOf(myId to myName, otherId to otherName),
            participantKeys = mapOf(myId to myPublicKey, otherId to otherPublicKey),
            isGroup = false,
            updatedAt = Timestamp.now()
        )
        chatsCollection.document(chatId).set(chat).await()
        return chatId
    }

    suspend fun isUserAdmin(userId: String): Boolean {
        return try {
            val userDoc = usersCollection.document(userId).get().await()
            userDoc.getBoolean("isAdmin") ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun getBlockedUsers(userId: String): Flow<List<String>> = callbackFlow {
        val subscription = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val blocked = snapshot?.get("blockedUsers") as? List<*> ?: emptyList<String>()
                trySend(blocked.filterIsInstance<String>())
            }
        awaitClose { subscription.remove() }
    }

    suspend fun blockUser(myId: String, targetId: String) {
        usersCollection.document(myId).update("blockedUsers", FieldValue.arrayUnion(targetId)).await()
    }

    suspend fun unblockUser(myId: String, targetId: String) {
        usersCollection.document(myId).update("blockedUsers", FieldValue.arrayRemove(targetId)).await()
    }

    suspend fun reportContent(contentId: String, type: String, reporterId: String) {
        val report = mapOf(
            "contentId" to contentId,
            "type" to type,
            "reporterId" to reporterId,
            "timestamp" to Timestamp.now()
        )
        reportsCollection.add(report).await()
    }
}
