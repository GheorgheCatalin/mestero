package com.mestero.ui.dashboard.listings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.mestero.data.models.ListingModel
import com.mestero.network.firestore.FirestoreQueryFilterType
import com.mestero.network.firestore.FirestoreRepository
import com.mestero.network.firestore.FirestoreQueryFilter
import com.mestero.network.firestore.FirestoreQueryParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ListingsViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _listingsState = MutableLiveData<ListingsUiState>()
    val listingsState: LiveData<ListingsUiState> = _listingsState

    fun loadListings(categoryId: String, subcategoryId: String) {
        _listingsState.value = ListingsUiState.Loading

        viewModelScope.launch {
            try {
                // Fetch listings by subcategory - only show active listings
                val queryParams = FirestoreQueryParams(
                    filters = listOf(
                        FirestoreQueryFilter(
                            field = "subcategory",
                            value = subcategoryId,
                            type = FirestoreQueryFilterType.EQUAL_TO
                        ),
                        FirestoreQueryFilter(
                            field = "active",
                            value = true,
                            type = FirestoreQueryFilterType.EQUAL_TO
                        )
                    ),
                    orderBy = "createdAt",
                    orderDirection = Query.Direction.DESCENDING
                )
                
                val querySnapshot = firestoreRepository.queryDocuments(
                    collection = ListingModel.COLLECTION_NAME,
                    params = queryParams
                )
                
                val listingsList = querySnapshot.documents.mapNotNull { doc ->
                    try {
                        ListingModel.fromFirestoreDocument(doc)
                    } catch (e: Exception) {
                        Log.w("ListingsViewModel", "Failed to parse listing document ${doc.id}", e)
                        null
                    }
                }

                _listingsState.value = if (listingsList.isEmpty()) {
                    ListingsUiState.Empty
                } else {
                    ListingsUiState.Success(listingsList)
                }

            } catch (e: Exception) {
                Log.e("ListingsViewModel", "Error loading listings for subcategory: $subcategoryId", e)
                _listingsState.value = ListingsUiState.Error(
                    "Failed to load listings: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    fun refreshListings(categoryId: String, subcategoryId: String) {
        loadListings(categoryId, subcategoryId)
    }
}


sealed interface ListingsUiState {
    data object Loading : ListingsUiState
    data class Success(val listings: List<ListingModel>) : ListingsUiState
    data object Empty : ListingsUiState
    data class Error(val message: String) : ListingsUiState
}
