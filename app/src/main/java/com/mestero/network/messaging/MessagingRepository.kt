package com.mestero.network.messaging

import androidx.lifecycle.LiveData
import com.mestero.data.models.ConversationModel
import com.mestero.data.models.MessageModel

interface MessagingRepository {
    suspend fun getOrCreateConversation(currentUserId: String, otherUserId: String): String
    fun observeConversations(userId: String): LiveData<List<ConversationModel>>
    fun observeMessages(conversationId: String): LiveData<List<MessageModel>>
    suspend fun sendMessage(conversationId: String, fromUserId: String, text: String)
    suspend fun markConversationAsRead(conversationId: String, userId: String)
    suspend fun getUserDisplayNames(userIds: List<String>): Map<String, String>
}