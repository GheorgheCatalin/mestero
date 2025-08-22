package com.mestero.ui.dashboard.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mestero.data.models.MessageModel
import com.mestero.network.firestore.FirestoreRepository
import com.mestero.network.auth.AccountService
import com.mestero.network.messaging.MessagingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messagingRepository: MessagingRepository,
    private val accountService: AccountService,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _messages = MutableLiveData<List<MessageModel>>(emptyList())
    val messages: LiveData<List<MessageModel>> = _messages

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _chatTitle = MutableLiveData<String>("Chat")
    val chatTitle: LiveData<String> = _chatTitle

    private var conversationId: String? = null
    private var observeJob: Job? = null
    val currentUserId: String get() = accountService.currentUserId


    fun startWithUser(otherUserId: String) {
        val uid = accountService.currentUserId
        if (uid.isEmpty()) {
            _error.value = "You must be logged in to chat"
            return
        }
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val convId = messagingRepository.getOrCreateConversation(uid, otherUserId)
                resolveOtherUserTitle(otherUserId)
                observeConversation(convId)
            } catch (e: Exception) {
                //_error.value = e.message
                _error.value =  "Failed to start chat"
                _isLoading.value = false
            }
        }
    }

    fun observeConversation(conversationId: String) {
        this.conversationId = conversationId
        _isLoading.value = true
        observeJob?.cancel()

        observeJob = viewModelScope.launch {
            // Try to get other user id from conversation id
            val otherId = getOtherUserIdFromConversationId(conversationId)
            if (otherId.isNotEmpty()) {
                resolveOtherUserTitle(otherId)
            }
            messagingRepository.observeMessages(conversationId).observeForever { list ->
                _messages.postValue(list)
                _isLoading.postValue(false)
            }
        }
    }

    fun sendMessage(text: String) {
        val convId = conversationId ?: return
        val uid = accountService.currentUserId
        if (uid.isEmpty() || text.isBlank())
            return

        viewModelScope.launch {
            try {
                messagingRepository.sendMessage(convId, uid, text.trim())
            } catch (e: Exception) {
                //_error.postValue("Failed to send message")
                _error.postValue(e.message ?: "Failed to send message")
            }
        }
    }

    fun markRead() {
        val convId = conversationId ?: return
        val uid = accountService.currentUserId
        if (uid.isEmpty())
            return

        viewModelScope.launch {
            try {
                messagingRepository.markConversationAsRead(convId, uid)
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Failed to mark read")
            }
        }
    }


    private suspend fun resolveOtherUserTitle(otherUserId: String) {
        try {
            val userDoc = firestoreRepository.getDocumentData("users", otherUserId)
            val first = userDoc?.getString("firstName").orEmpty()
            val last = userDoc?.getString("lastName").orEmpty()
            val display = ("$first $last").trim().ifEmpty { "Chat" }

            _chatTitle.postValue(display)
        } catch (_: Exception) {
            _chatTitle.postValue("Chat")
        }
    }

    private fun getOtherUserIdFromConversationId(conversationId: String): String {
        val uid = accountService.currentUserId
        val parts = conversationId.split("_")
        return when (parts.size) {
            2 -> {
                when (uid) {
                    parts[0] -> parts[1]
                    parts[1] -> parts[0]
                    else -> ""
                }
            }
            else -> ""
        }
    }
}