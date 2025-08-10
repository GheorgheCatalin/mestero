package com.mestero.ui.dashboard.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mestero.data.UserType
import com.mestero.data.models.Category
import com.mestero.data.models.CategoryManager
import com.mestero.data.models.ListingModel
import com.mestero.network.auth.AccountService
import com.mestero.network.firestore.FirestoreQueryFilterType
import com.mestero.network.firestore.FirestoreRepository
import com.mestero.network.firestore.FirestoreQueryFilter
import com.mestero.network.firestore.FirestoreQueryParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val accountService: AccountService
) : ViewModel() {

    private val _userType = MutableLiveData<UserType>()
    val userType: LiveData<UserType> = _userType

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _newPosts = MutableLiveData<List<ListingModel>>()
    val newPosts: LiveData<List<ListingModel>> = _newPosts

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _lastSeen = MutableLiveData<List<ListingModel>>()
    val lastSeen: LiveData<List<ListingModel>> = _lastSeen

    private val _myPosts = MutableLiveData<List<ListingModel>>()
    val myPosts: LiveData<List<ListingModel>> = _myPosts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadUserData()
    }

    private fun loadUserData() {
        if (accountService.currentUserId.isEmpty()) {
            _userType.value = UserType.CLIENT
            _userName.value = "Guest"
            loadData()
            return
        }

        _isLoading.value = true

        accountService.fetchUserData(
            onResult = { userType, firstName ->
                _userType.value = userType
                _userName.value = firstName
                loadData()
            }
        )
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load categories (first 6 for horizontal display)
                _categories.value = CategoryManager.categories.take(6)
                
                // Load new posts (latest 10) // TODO can reduce amount
                loadNewPosts()
                
                // Load role-specific data
                when (_userType.value) {
                    UserType.CLIENT -> {
                        loadLastSeenPosts()
                    }
                    UserType.PROVIDER -> {
                        loadMyPosts()
                    }
                    null -> {
                        // Default to client behavior
                        loadLastSeenPosts()
                    }
                }
            } catch (e: Exception) {
                // TODO: Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadNewPosts() {
        try {
            val queryParams = FirestoreQueryParams(
                orderBy = "createdAt",
                orderDirection = com.google.firebase.firestore.Query.Direction.DESCENDING,
                limit = 10
            )
            
            val querySnapshot = firestoreRepository.queryDocuments(ListingModel.COLLECTION_NAME, queryParams)
            val listings = querySnapshot.documents.mapNotNull { doc ->
                try {
                    ListingModel.fromFirestoreDocument(doc)
                } catch (e: Exception) {
                    null
                }
            }
            
            _newPosts.postValue(listings)
        } catch (e: Exception) {
            _newPosts.postValue(emptyList())
        }
    }

    private suspend fun loadLastSeenPosts() {
        // TODO implement actual last seen tracking
        // For now, just load some listings
        try {
            val queryParams = FirestoreQueryParams(
                orderBy = "views",
                orderDirection = com.google.firebase.firestore.Query.Direction.DESCENDING,
                limit = 8
            )
            
            val querySnapshot = firestoreRepository.queryDocuments(ListingModel.COLLECTION_NAME, queryParams)
            val listings = querySnapshot.documents.mapNotNull { doc ->
                try {
                    ListingModel.fromFirestoreDocument(doc)
                } catch (e: Exception) {
                    null
                }
            }
            
            _lastSeen.postValue(listings)
        } catch (e: Exception) {
            _lastSeen.postValue(emptyList())
        }
    }

    private suspend fun loadMyPosts() {
        if (accountService.currentUserId.isEmpty()) {
            _myPosts.postValue(emptyList())
            return
        }

        try {
            val queryParams = FirestoreQueryParams(
                filters = listOf(
                    FirestoreQueryFilter(
                        field = "providerId",
                        value = accountService.currentUserId,
                        type = FirestoreQueryFilterType.EQUAL_TO
                    )
                ),
                orderBy = "createdAt",
                orderDirection = com.google.firebase.firestore.Query.Direction.DESCENDING,
                limit = 8
            )
            
            val querySnapshot = firestoreRepository.queryDocuments(ListingModel.COLLECTION_NAME, queryParams)
            val listings = querySnapshot.documents.mapNotNull { doc ->
                try {
                    ListingModel.fromFirestoreDocument(doc)
                } catch (e: Exception) {
                    null
                }
            }
            
            _myPosts.postValue(listings)
        } catch (e: Exception) {
            _myPosts.postValue(emptyList())
        }
    }

    fun refreshData() {
        loadUserData()
    }
}