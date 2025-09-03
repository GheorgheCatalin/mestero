package com.mestero.network.messaging

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldPath.documentId
import com.google.firebase.firestore.FieldValue

import com.google.firebase.firestore.Query
import com.mestero.constants.FirestoreCollections
import com.mestero.data.models.ConversationModel
import com.mestero.data.models.MessageModel
import com.mestero.network.firestore.FirestoreRepository
import com.mestero.utils.Analytics
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagingRepositoryImpl @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : MessagingRepository {

    override suspend fun getOrCreateConversation(
        currentUserId: String,
        otherUserId: String
    ): String {
        // Generate conversation ID with current user first (consistent ordering)
        val conversationId = "${currentUserId}_${otherUserId}"
        val convReference = firestoreRepository
            .getCollectionReference(ConversationModel.COLLECTION_NAME)
            .document(conversationId)

        // Check if conversation already exists in DB
        val existingDoc = convReference.get().await()
        if (existingDoc.exists()) {
            return conversationId
        }

        val data = mapOf(
            "participants" to listOf(currentUserId, otherUserId),
            "lastMessage" to "",
            "lastMessageAt" to FieldValue.serverTimestamp(),
            "lastSenderId" to "",
            "unreadCounts" to mapOf(currentUserId to 0L, otherUserId to 0L),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        convReference.set(data).await()
        return conversationId
    }

    override fun observeConversations(userId: String): LiveData<List<ConversationModel>> {
        val liveData = MutableLiveData<List<ConversationModel>>()

        val ref = firestoreRepository.getCollectionReference(ConversationModel.COLLECTION_NAME)
            .whereArrayContains("participants", userId)
            .orderBy("lastMessageAt", Query.Direction.DESCENDING)

        ref.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("MessagingRepo", "observeConversations error", error)
                return@addSnapshotListener
            }

            if (snapshots == null)
                return@addSnapshotListener

            val items = snapshots.documents.mapNotNull {
                ConversationModel.fromFirestoreDocument(it)
            }
            liveData.postValue(items)
        }
        return liveData
    }

    override fun observeMessages(conversationId: String): LiveData<List<MessageModel>> {
        val liveData = MutableLiveData<List<MessageModel>>()

        val messagesRef = firestoreRepository
            .getCollectionReference(ConversationModel.COLLECTION_NAME)
            .document(conversationId)
            .collection(FirestoreCollections.MESSAGES)
            .orderBy("createdAt", Query.Direction.ASCENDING)

        messagesRef.addSnapshotListener { snapshots, _ ->
            val items = snapshots?.documents?.mapNotNull { doc ->
                MessageModel.fromFirestoreDocument(doc)
            } ?: emptyList()

            liveData.postValue(items)
        }
        return liveData
    }

    override suspend fun sendMessage(conversationId: String, fromUserId: String, text: String) {
        val convDoc = firestoreRepository.getCollectionReference(ConversationModel.COLLECTION_NAME)
            .document(conversationId)

        // Fetch the other participant from convo id (deterministic id scheme )
        val parts = conversationId.split("_")
        val otherUserId = if (parts.size == 2) {
            if (parts[0] == fromUserId) {
                parts[1]
            } else {
                parts[0]
            }
        } else {
            // Fallback read the ID of the other user from doc
            val conversationSnapshot = convDoc.get().await()
            val participants = (conversationSnapshot.get("participants") as? List<*>)
                ?.mapNotNull { it as? String }
                ?: emptyList()
            participants.firstOrNull { it != fromUserId } ?: ""
        }

        // Update conversation with the new message - with analytics
        Analytics.measureAndLogSuspend(
            eventName = Analytics.Events.SEND_MESSAGE,
            additionalParams = mapOf(
                "message_length" to text.length,
                "conversation_id" to conversationId,
                "has_other_user" to otherUserId.isNotEmpty()
            )
        ) {
            val msgData = mapOf(
                "senderId" to fromUserId,
                "text" to text,
                "createdAt" to FieldValue.serverTimestamp(),
                "readBy" to listOf(fromUserId)
            )
            convDoc.collection(FirestoreCollections.MESSAGES).add(msgData).await()

            val updates = hashMapOf<String, Any>(
                "lastMessage" to text,
                "lastMessageAt" to FieldValue.serverTimestamp(),
                "lastSenderId" to fromUserId,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (otherUserId.isNotEmpty()) {
                updates["unreadCounts.$otherUserId"] = FieldValue.increment(1)
            }
            updates["unreadCounts.$fromUserId"] = 0L

            convDoc.update(updates as Map<String, Any>).await()
        }
    }

    override suspend fun markConversationAsRead(conversationId: String, userId: String) {
        val convDoc = firestoreRepository.getCollectionReference(ConversationModel.COLLECTION_NAME)
            .document(conversationId)

        convDoc.update(mapOf("unreadCounts.$userId" to 0L)).await()
    }

    override suspend fun getUserDisplayNames(userIds: List<String>): Map<String, String> {
        if (userIds.isEmpty())
            return emptyMap()

        val ids = userIds.distinct()
        val displayNamesResult = mutableMapOf<String, String>()

        val snap = firestoreRepository.getCollectionReference(FirestoreCollections.USERS)
            .whereIn(documentId(), ids)
            .get()
            .await()
        
        for (doc in snap.documents) {
            val first = doc.getString("firstName").orEmpty()
            val last = doc.getString("lastName").orEmpty()
            val name = ("$first $last").trim()
            if (name.isNotEmpty()) {
                displayNamesResult[doc.id] = name
            }
        }
        
        // Fill any missing names with "Unknown User
        ids.filterNot { it in displayNamesResult.keys }
            .forEach {
            displayNamesResult[it] = "Unknown User"
        }
        return displayNamesResult
    }
}