package com.mestero.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot

data class MessageModel(
    @DocumentId
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val createdAt: Timestamp? = null,
    val readBy: List<String> = emptyList()
) {
    companion object {
        fun fromMap(map: Map<String, Any?>): MessageModel {
            return MessageModel(
                id = map["id"] as? String ?: "",
                senderId = map["senderId"] as? String ?: "",
                text = map["text"] as? String ?: "",
                createdAt = map["createdAt"] as? Timestamp,
                readBy = (map["readBy"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
            )
        }

        fun fromFirestoreDocument(document: DocumentSnapshot): MessageModel? {
            return try {
                val data = document.data ?: return null
                fromMap(data).copy(id = document.id)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "senderId" to senderId,
        "text" to text,
        "createdAt" to createdAt,
        "readBy" to readBy
    )
}