package com.mestero.ui.dashboard.home

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.mestero.data.UserType
import com.mestero.data.models.Category
import com.mestero.data.models.CategoryManager
import com.mestero.data.models.ListingModel
import com.mestero.network.auth.AccountService
import com.mestero.network.firestore.FirestoreQueryFilterType
import com.mestero.network.firestore.FirestoreRepository
import com.mestero.network.firestore.FirestoreQueryFilter
import com.mestero.network.firestore.FirestoreQueryParams
import com.mestero.utils.Analytics
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

    private val _mostViewed = MutableLiveData<List<ListingModel>>()
    val mostViewed: LiveData<List<ListingModel>> = _mostViewed

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
                        loadMostViewedPosts()
                    }
                    UserType.PROVIDER -> {
                        loadMyPosts()
                    }
                    null -> {
                        // Default to client behavior
                        loadMostViewedPosts()
                    }
                }
            } catch (e: Exception) {
                // TODO Handle error
                Log.e("ClientBookingsViewModel", "Failed to load data", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadNewPosts() {
        try {
            val listings = Analytics.measureListingsLoad("new_posts") {
                val queryParams = FirestoreQueryParams(
                    orderBy = "createdAt",
                    orderDirection = Query.Direction.DESCENDING,
                    limit = 10
                )
                
                val querySnapshot = firestoreRepository.queryDocuments(ListingModel.COLLECTION_NAME, queryParams)
                querySnapshot.documents.mapNotNull { doc ->
                    try {
                        ListingModel.fromFirestoreDocument(doc)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            
            _newPosts.postValue(listings)
        } catch (e: Exception) {
            _newPosts.postValue(emptyList())
        }
    }

    private suspend fun loadMostViewedPosts() {
        try {
            val listings = Analytics.measureListingsLoad("most_viewed") {
                val queryParams = FirestoreQueryParams(
                    filters = listOf(
                        FirestoreQueryFilter(
                            field = "active",
                            value = true,
                            type = FirestoreQueryFilterType.EQUAL_TO
                        )
                    ),
                    orderBy = "views",
                    orderDirection = Query.Direction.DESCENDING,
                    limit = 8
                )
                
                val querySnapshot = firestoreRepository.queryDocuments(ListingModel.COLLECTION_NAME, queryParams)
                querySnapshot.documents.mapNotNull { doc ->
                    try {
                        ListingModel.fromFirestoreDocument(doc)
                    } catch (e: Exception){
                        null
                    }
                }
            }
            
            _mostViewed.postValue(listings)
        } catch (e: Exception) {
            _mostViewed.postValue(emptyList())
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
                orderDirection = Query.Direction.DESCENDING,
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