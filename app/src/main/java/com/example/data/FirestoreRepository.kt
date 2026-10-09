package com.example.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.functions.FirebaseFunctions
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.HttpsCallableResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class PublicPlayer(
    val uid: String = "",
    val displayName: String = "Player",
    val countryCode: String = "",
    val avatarUrl: String = "",
    val level: Long = 1,
    val xp: Long = 0,
    val stars: Long = 0,
    val bestTime: Long = -1,
    val bestMoves: Long = -1
)

data class FirestoreNotification(
    val id: String = "",
    val type: String = "system",
    val title: String = "",
    val message: String = "",
    val read: Boolean = false,
    val createdAt: com.google.firebase.Timestamp? = null
)

data class DailyLoginState(
    val currentDay: Long = 1,
    val claimedDays: List<Long> = emptyList()
)

data class DailyLoginReward(val day: Long = 0, val coins: Long = 0, val enabled: Boolean = true)

class FirestoreRepository(private val firestore: FirebaseFirestore) {
    private val functions = FirebaseFunctions.getInstance()

    fun observeDailyLogin(uid: String): Flow<DailyLoginState> = callbackFlow {
        val registration = firestore.collection("users").document(uid).collection("dailyLogin").document("state")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val data = snapshot?.data.orEmpty()
                trySend(DailyLoginState(
                    currentDay = (data["currentDay"] as? Number)?.toLong() ?: 1,
                    claimedDays = (data["claimedDays"] as? List<*>)?.mapNotNull { (it as? Number)?.toLong() }.orEmpty()
                ))
            }
        awaitClose { registration.remove() }
    }

    fun observeDailyLoginRewards(): Flow<List<DailyLoginReward>> = callbackFlow {
        val registration = firestore.collection("dailyLoginRewards").whereEqualTo("enabled", true)
            .orderBy("day").addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.documents.orEmpty().mapNotNull { it.toObject(DailyLoginReward::class.java) })
            }
        awaitClose { registration.remove() }
    }

    fun claimDailyLoginReward(day: Long): Task<HttpsCallableResult> =
        functions.getHttpsCallable("claimDailyLoginReward").call(mapOf("day" to day))

    fun claimAdReward(rewardType: String, adSessionId: String, levelId: Int? = null): Task<HttpsCallableResult> = {
        val params = mutableMapOf<String, Any>(
            "rewardType" to rewardType,
            "adSessionId" to adSessionId
        )
        levelId?.let { params["levelId"] = it }
        functions.getHttpsCallable("claimAdReward").call(params)
    }()

    fun claimDailyLoginRewardWithMultiplier(day: Long, multiplier: Int, adSessionId: String? = null): Task<HttpsCallableResult> = {
        val params = mutableMapOf<String, Any>(
            "day" to day,
            "multiplier" to multiplier
        )
        adSessionId?.let { params["adSessionId"] = it }
        functions.getHttpsCallable("claimDailyLoginReward").call(params)
    }()

    fun completeLevel(levelId: Int, moves: Int, elapsedTime: Int, hintsUsed: Int, undosUsed: Int): Task<HttpsCallableResult> =
        functions.getHttpsCallable("completeLevel").call(mapOf(
            "levelId" to levelId,
            "moves" to moves,
            "elapsedTime" to elapsedTime,
            "hintsUsed" to hintsUsed,
            "undosUsed" to undosUsed
        ))
    fun saveGameSession(uid: String, sessionId: String, data: Map<String, Any?>) {
        firestore.collection("users").document(uid).collection("gameSessions").document(sessionId).set(data)
    }

    fun finishGameSession(uid: String, sessionId: String, status: String) {
        firestore.collection("users").document(uid).collection("gameSessions").document(sessionId)
            .update(mapOf("status" to status, "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()))
    }
    fun observePublicProfile(uid: String): Flow<PublicPlayer?> = callbackFlow {
        val registration = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(PublicPlayer::class.java)?.copy(uid = uid))
            }
        awaitClose { registration.remove() }
    }

    fun observeLeaderboard(countryCode: String? = null): Flow<List<PublicPlayer>> = callbackFlow {
        var query: Query = firestore.collection("users")
            .whereEqualTo("publicProfile", true)
            .orderBy("xp", Query.Direction.DESCENDING)
            .limit(100)
        if (!countryCode.isNullOrBlank()) query = query.whereEqualTo("countryCode", countryCode)

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val players = snapshot?.documents.orEmpty().mapNotNull { document ->
                document.toObject(PublicPlayer::class.java)?.copy(uid = document.id)
            }
            trySend(players)
        }
        awaitClose { registration.remove() }
    }

    fun observeNotifications(uid: String): Flow<List<FirestoreNotification>> = callbackFlow {
        val registration = firestore.collection("users").document(uid)
            .collection("notifications")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents.orEmpty().mapNotNull { document ->
                    document.toObject(FirestoreNotification::class.java)?.copy(id = document.id)
                })
            }
        awaitClose { registration.remove() }
    }

    fun markNotificationRead(uid: String, notificationId: String) {
        firestore.collection("users").document(uid)
            .collection("notifications").document(notificationId)
            .update("read", true)
    }

    fun markAllNotificationsRead(uid: String) {
        firestore.collection("users").document(uid).collection("notifications")
            .whereEqualTo("read", false).get().addOnSuccessListener { snapshot ->
                val batch = firestore.batch()
                snapshot.documents.forEach { batch.update(it.reference, "read", true) }
                batch.commit()
            }
    }

    fun updatePublicProfile(uid: String, displayName: String, countryCode: String, avatarUrl: String) {
        firestore.collection("users").document(uid).update(
            mapOf(
                "displayName" to displayName,
                "countryCode" to countryCode,
                "avatarUrl" to avatarUrl,
                "publicProfile" to true
            )
        )
    }
}