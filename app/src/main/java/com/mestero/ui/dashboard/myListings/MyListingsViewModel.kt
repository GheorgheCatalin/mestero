package com.mestero.ui.dashboard.myListings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.mestero.data.models.ListingModel
import com.mestero.network.auth.AccountService
import com.mestero.network.firestore.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyListingsViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val accountService: AccountService
) : ViewModel() {
    private val _myListingsState= MutableLiveData<MyListingsResult>()
    val myListingsState: LiveData<MyListingsResult> = _myListingsState

    init {
        loadListings()
    }

    private fun loadListings() {
        val currentUserId = accountService.currentUserId
        
        if (currentUserId.isEmpty()) {
            _myListingsState.value = MyListingsResult.Empty
            return
        }
        
        _myListingsState.value = MyListingsResult.Loading

        viewModelScope.launch {
            try {
                // Use server-side sorting
                val querySnapshot = firestoreRepository.queryByFieldWithOrder(
                    collection = ListingModel.COLLECTION_NAME,
                    field = "providerId", 
                    value = currentUserId,
                    orderBy = "createdAt",
                    orderDirection = Query.Direction.DESCENDING
                )
                
                val listingsList = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(ListingModel::class.java)?.copy(id = doc.id)
                }

                _myListingsState.postValue(
                    if (listingsList.isEmpty()) {
                        MyListingsResult.Empty
                    } else {
                        MyListingsResult.Success(listingsList)
                    }
                )
            } catch (e: Exception) {
                Log.e("MyListingsViewModel", "Error loading listings", e)
                _myListingsState.postValue(
                    MyListingsResult.Error("Failed to load listings: ${e.message ?: "Unknown error"}")
                )
            }
        }
    }

    fun refreshListings() {
        loadListings()
    }
}


sealed interface MyListingsResult {
    data object Loading : MyListingsResult
    data class Success(val listings: List<ListingModel>) : MyListingsResult
    data object Empty : MyListingsResult  // TODO delete ?
    data class Error(val message: String) : MyListingsResult
}