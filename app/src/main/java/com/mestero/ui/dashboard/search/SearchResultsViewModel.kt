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
import com.mestero.utils.Analytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import com.mestero.R
import dagger.hilt.android.qualifiers.ApplicationContext


@HiltViewModel
class SearchResultsViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _searchState = MutableLiveData<SearchResultsUiState>()
    val searchState: LiveData<SearchResultsUiState> = _searchState

    // Due to Firestore limitations get all active listings and filter on client-side for text matches
    fun searchListings(query: String) {
        if (query.isBlank()) {
            _searchState.value = SearchResultsUiState.Empty(context.getString(R.string.error_search_term_required))
            return
        }

        _searchState.value = SearchResultsUiState.Loading

        viewModelScope.launch {
            val searchResults = Analytics.measureAndLogSuspend(
                eventName = Analytics.Events.SEARCH_PERFORMED,
                additionalParams = mapOf(
                    Analytics.Params.SEARCH_QUERY to query.trim(),
                    Analytics.Params.SEARCH_TYPE to "general"
                )
            ) {
                try {
                    performFirebaseSearch(query.trim())
                } catch (e: Exception) {
                    _searchState.value = SearchResultsUiState.Error(
                        "Error searching: ${e.message ?: "Unknown error"}"
                    )
                    emptyList()
                }
            }
            
            if (searchResults.isEmpty()) {
                _searchState.value = SearchResultsUiState.Empty(
                    "No services found for \"$query\""
                )
            } else {
                _searchState.value = SearchResultsUiState.Success(searchResults)
                
                // Log search success in analytics
                Analytics.logEvent(
                    Analytics.Events.SEARCH_RESULTS,
                    mapOf(
                        Analytics.Params.SEARCH_QUERY to query.trim(),
                        Analytics.Params.RESULTS_COUNT to searchResults.size,
                        Analytics.Params.SEARCH_TYPE to "general"
                    )
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

        // Measure time for Firestore search query
        val allListings = Analytics.measureFirestoreQuery("search_all_listings") {
            val firestoreResult = firestoreRepository.queryDocuments(
                ListingModel.COLLECTION_NAME, 
                queryParams
            )

            firestoreResult.documents.mapNotNull { document ->
                try {
                    ListingModel.fromFirestoreDocument(document)
                } catch (e: Exception) {
                    // Skip invalid docs
                    null
                }
            }
        }

        // Client-side filtering for text search
        val searchTerm = query.lowercase()
        return Analytics.measureAndLog("search_client_filtering") {
            allListings.filter { listing ->
                searchTerm in listing.title.lowercase() ||
                searchTerm in listing.description.lowercase() ||
                searchTerm in listing.category.lowercase() ||
                searchTerm in listing.subcategory.lowercase() ||
                listing.tags.any { tag -> searchTerm in tag.lowercase() }
            }
        }
    }

    fun searchInSpecificCategory(query: String, categoryId: String) {
        if (query.isBlank()) {
            _searchState.value = SearchResultsUiState.Empty("Please enter a search term")
            return
        }

        _searchState.value = SearchResultsUiState.Loading

        viewModelScope.launch {
            val filteredResults = Analytics.measureAndLogSuspend(
                eventName = Analytics.Events.SEARCH_PERFORMED,
                additionalParams = mapOf(
                    Analytics.Params.SEARCH_QUERY to query.trim(),
                    Analytics.Params.SEARCH_TYPE to "category",
                    Analytics.Params.CATEGORY to categoryId
                )
            ) {
                try {
                    val queryParams = FirestoreQueryParams(
                        filters = listOf(
                            FirestoreQueryFilter("active", true, FirestoreQueryFilterType.EQUAL_TO),
                            FirestoreQueryFilter("category", categoryId, FirestoreQueryFilterType.EQUAL_TO)
                        ),
                        limit = 200
                    )

                    // Measure Firestore query time for category search
                    val categoryListings = Analytics.measureFirestoreQuery("search_category_listings") {
                        val firestoreResult = firestoreRepository.queryDocuments(
                            ListingModel.COLLECTION_NAME, 
                            queryParams
                        )

                        firestoreResult.documents.mapNotNull { document ->
                            try {
                                ListingModel.fromFirestoreDocument(document)
                            } catch (e: Exception) {
                                // Skip invalid docs
                                null
                            }
                        }
                    }

                    // Measure client-side filtering time for category search
                    val searchTerm = query.lowercase()
                    Analytics.measureAndLog("search_category_filtering") {
                        categoryListings.filter { listing ->
                            searchTerm in listing.title.lowercase() ||
                            searchTerm in listing.description.lowercase() ||
                            listing.tags.any { tag -> searchTerm in tag.lowercase() }
                        }
                    }
                } catch (e: Exception) {
                    _searchState.value = SearchResultsUiState.Error(
                        "Error searching: ${e.message ?: "Unknown error"}"
                    )
                    emptyList()
                }
            }

            if (filteredResults.isEmpty()) {
                _searchState.value = SearchResultsUiState.Empty(
                    "No services found for \"$query\" in this category"
                )
            } else {
                _searchState.value = SearchResultsUiState.Success(filteredResults)
                
                // Log category search success
                Analytics.logEvent(
                    Analytics.Events.SEARCH_RESULTS,
                    mapOf(
                        Analytics.Params.SEARCH_QUERY to query.trim(),
                        Analytics.Params.RESULTS_COUNT to filteredResults.size,
                        Analytics.Params.SEARCH_TYPE to "category",
                        Analytics.Params.CATEGORY to categoryId
                    )
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