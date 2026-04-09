package com.harc.health.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.harc.health.model.HealthLog
import com.harc.health.model.User
import kotlinx.coroutines.tasks.await

/**
 * Expert-Level Lean Backend Implementation
 * 
 * Strategy:
 * 1. Minimal Writes: Store only core recovery data.
 * 2. Single Reads: Avoid costly real-time listeners where possible.
 * 3. Local-First: Compute intelligence on-device, store results here.
 */
class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()
    private val logsCollection = db.collection("health_logs")
    private val usersCollection = db.collection("users")

    suspend fun saveLog(userId: String, log: HealthLog) {
        if (userId == "anonymous") return
        try {
            // Document ID = userId_date (Ensures 1 log per user per day)
            val docId = "${userId}_${log.date}"
            logsCollection.document(docId).set(log, SetOptions.merge()).await()
            Log.d("FirestoreRepository", "Log synced for $userId on ${log.date}")
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error syncing log: ${e.message}")
        }
    }

    suspend fun getRecentLogs(userId: String, limit: Long = 7): List<HealthLog> {
        if (userId == "anonymous") return emptyList()
        return try {
            val snapshot = logsCollection
                .whereEqualTo("userId", userId)
                .orderBy("date")
                .limit(limit)
                .get()
                .await()
            snapshot.toObjects(HealthLog::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error fetching logs: ${e.message}")
            emptyList()
        }
    }

    suspend fun saveUserProfile(user: User) {
        if (user.id == "anonymous" || user.id.isEmpty()) return
        try {
            usersCollection.document(user.id).set(user, SetOptions.merge()).await()
            Log.d("FirestoreRepository", "Profile synced for ${user.id}")
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error syncing profile: ${e.message}")
        }
    }

    suspend fun getUserProfile(userId: String): User? {
        if (userId == "anonymous") return null
        return try {
            val snapshot = usersCollection.document(userId).get().await()
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error fetching profile: ${e.message}")
            null
        }
    }

    suspend fun deleteUserData(userId: String) {
        if (userId == "anonymous") return
        try {
            // Delete user profile
            usersCollection.document(userId).delete().await()

            // Delete user logs (Batch delete to save on operations)
            val logs = logsCollection.whereEqualTo("userId", userId).get().await()
            if (!logs.isEmpty) {
                db.runBatch { batch ->
                    logs.documents.forEach { batch.delete(it.reference) }
                }.await()
            }
            Log.d("FirestoreRepository", "All cloud data purged for $userId")
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error purging cloud data: ${e.message}")
            throw e
        }
    }
}
