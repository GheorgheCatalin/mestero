package com.mestero.ui.dashboard.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mestero.data.models.ConversationModel
import com.mestero.network.auth.AccountService
import com.mestero.network.messaging.MessagingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@HiltViewModel
class ConversationsViewModel @Inject constructor(
    private val messagingRepository: MessagingRepository,
    private val accountService: AccountService
) : ViewModel() {

    val currentUserId: String
        get() = accountService.currentUserId

    private val _conversations = MutableLiveData<List<ConversationModel>>(emptyList())
    val conversations: LiveData<List<ConversationModel>> = _conversations

    private val _userDisplayNames = MutableLiveData<Map<String, String>>(emptyMap())
    val userNames: LiveData<Map<String, String>> = _userDisplayNames

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private var observeJob: Job? = null

    fun startObserving() {
        val uid = accountService.currentUserId
        if (uid.isEmpty()) return

        _isLoading.value = true
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            messagingRepository.observeConversations(uid).observeForever { list ->

                _conversations.postValue(list)
                _isLoading.postValue(false)

                // Fetch display names for convos
                val participantUserIds = list.mapNotNull { conversation -> 
                    conversation.participants.firstOrNull { participantId ->
                        participantId != uid
                    }
                }
                val alreadyCachedNames = _userDisplayNames.value ?: emptyMap()
                val newIds = participantUserIds.filter { userId ->
                    userId !in alreadyCachedNames
                }
                
                if (newIds.isNotEmpty()) {
                    viewModelScope.launch {
                        val newlyFetchedNames = messagingRepository.getUserDisplayNames(newIds)
                        if (newlyFetchedNames.isNotEmpty()) {
                            _userDisplayNames.postValue(alreadyCachedNames + newlyFetchedNames)
                        }
                    }
                }

            }
        }
    }
}