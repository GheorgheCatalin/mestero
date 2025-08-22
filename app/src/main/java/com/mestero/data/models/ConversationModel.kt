package com.mestero.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot

data class ConversationModel(
    @DocumentId
    val id: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageAt: Timestamp? = null,
    val lastSenderId: String = "",
    // Unread message counts per each of the 2 participants
    // Sender’s count resets to 0; recipient’s is increment by 1
    val unreadMessageCount: Map<String, Long> = emptyMap(),
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {

    companion object {
        const val COLLECTION_NAME = "conversations"

        fun fromMap(map: Map<String, Any?>): ConversationModel {
            return ConversationModel(
                id = map["id"] as? String ?: "",
                participants = (map["participants"] as? List<*>)?.mapNotNull { it as? String }
                    ?: emptyList(),
                lastMessage = map["lastMessage"] as? String ?: "",
                lastMessageAt = map["lastMessageAt"] as? Timestamp,
                lastSenderId = map["lastSenderId"] as? String ?: "",
                unreadMessageCount = (map["unreadCounts"] as? Map<*, *>)
                    ?.mapNotNull { (k, v) -> (k as? String)?.let { it to (v as? Number)?.toLong() } }
                    ?.mapNotNull { (key, value) -> value?.let { key to it } } // non-null Long
                    ?.toMap() ?: emptyMap(),
                createdAt = map["createdAt"] as? Timestamp,
                updatedAt = map["updatedAt"] as? Timestamp
            )
        }

        fun fromFirestoreDocument(document: DocumentSnapshot): ConversationModel? {
            return try {
                val data = document.data ?: return null
                fromMap(data).copy(id = document.id)
            } catch (_: Exception) {
                null
            }
        }
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "participants" to participants,
        "lastMessage" to lastMessage,
        "lastMessageAt" to lastMessageAt,
        "lastSenderId" to lastSenderId,
        "unreadCounts" to unreadMessageCount,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )


    fun getUnreadCountsForDisplay(): Map<String, Long> {
        return unreadMessageCount
    }
}