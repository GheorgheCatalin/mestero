package com.mestero.ui.dashboard.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mestero.data.models.ListingModel
import com.mestero.network.firestore.FirestoreQueryFilter
import com.mestero.network.firestore.FirestoreQueryFilterType
import com.mestero.network.firestore.FirestoreQueryParams
import com.mestero.network.firestore.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchResultsViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _searchState = MutableLiveData<SearchResultsUiState>()
    val searchState: LiveData<SearchResultsUiState> = _searchState

    // Due to Firestore limitations get all active listings and filter on client-side for text matches
    fun searchListings(query: String) {
        if (query.isBlank()) {
            _searchState.value = SearchResultsUiState.Empty("Please enter a search term")
            return
        }

        _searchState.value = SearchResultsUiState.Loading

        viewModelScope.launch {
            try {
                val searchResults = performFirebaseSearch(query.trim())
                
                if (searchResults.isEmpty()) {
                    _searchState.value = SearchResultsUiState.Empty(
                        "No services found for \"$query\""
                    )
                } else {
                    _searchState.value = SearchResultsUiState.Success(searchResults)
                }
            } catch (e: Exception) {
                _searchState.value = SearchResultsUiState.Error(
                    "Error searching: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    // Searches in title, description, category, and subcategory
    private suspend fun performFirebaseSearch(query: String): List<ListingModel> {
        val queryParams = FirestoreQueryParams(
            filters = listOf(
                FirestoreQueryFilter("active", true, FirestoreQueryFilterType.EQUAL_TO)
            ),
            limit = 200
        )

        val firestoreResult = firestoreRepository.queryDocuments(
            ListingModel.COLLECTION_NAME, 
            queryParams
        )

        val allListings = firestoreResult.documents.mapNotNull { document ->
            try {
                ListingModel.fromFirestoreDocument(document)
            } catch (e: Exception) {
                // Skip invalid docs
                null
            }
        }

        // Client-side filtering for text search
        val searchTerm = query.lowercase()
        return allListings.filter { listing ->
            searchTerm in listing.title.lowercase() ||
            searchTerm in listing.description.lowercase() ||
            searchTerm in listing.category.lowercase() ||
            searchTerm in listing.subcategory.lowercase() ||
            listing.tags.any { tag -> searchTerm in tag.lowercase() }
        }
    }

    fun searchInSpecificCategory(query: String, categoryId: String) {
        if (query.isBlank()) {
            _searchState.value = SearchResultsUiState.Empty("Please enter a search term")
            return
        }

        _searchState.value = SearchResultsUiState.Loading

        viewModelScope.launch {
            try {
                val queryParams = FirestoreQueryParams(
                    filters = listOf(
                        FirestoreQueryFilter("active", true, FirestoreQueryFilterType.EQUAL_TO),
                        FirestoreQueryFilter("category", categoryId, FirestoreQueryFilterType.EQUAL_TO)
                    ),
                    limit = 200
                )

                val firestoreResult = firestoreRepository.queryDocuments(
                    ListingModel.COLLECTION_NAME, 
                    queryParams
                )

                val categoryListings = firestoreResult.documents.mapNotNull { document ->
                    try {
                        ListingModel.fromFirestoreDocument(document)
                    } catch (e: Exception) {
                        // Skip invalid docs
                        null
                    }
                }

                val searchTerm = query.lowercase()
                val filteredResults = categoryListings.filter { listing ->
                    searchTerm in listing.title.lowercase() ||
                    searchTerm in listing.description.lowercase() ||
                    listing.tags.any { tag -> searchTerm in tag.lowercase() }
                }

                if (filteredResults.isEmpty()) {
                    _searchState.value = SearchResultsUiState.Empty(
                        "No services found for \"$query\" in this category"
                    )
                } else {
                    _searchState.value = SearchResultsUiState.Success(filteredResults)
                }
            } catch (e: Exception) {
                _searchState.value = SearchResultsUiState.Error(
                    "Error searching: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }
}


sealed class SearchResultsUiState {
    data object Loading : SearchResultsUiState()
    data class Success(val listings: List<ListingModel>) : SearchResultsUiState()
    data class Empty(val message: String) : SearchResultsUiState()
    data class Error(val message: String) : SearchResultsUiState()
}