package com.mestero.ui.dashboard.addListing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mestero.data.UserType
import com.mestero.data.models.ListingModel
import com.mestero.network.auth.AccountService
import com.mestero.network.firestore.FirestoreQueryFilterType
import com.mestero.network.firestore.FirestoreRepository
import com.mestero.network.firestore.FirestoreQueryFilter
import com.mestero.network.firestore.FirestoreQueryParams
import com.mestero.utils.Analytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class AddListingViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val accountService: AccountService
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _createListingResult = MutableLiveData<Result<String>?>()
    val createListingResult: LiveData<Result<String>?> = _createListingResult

    init {
        // Additional defensive check - this should never fail due to how nav is handled
        require(accountService.currentUserId.isNotEmpty()) {
            "AddListing accessed without authenticated user"
        }
    }

    fun createListing(listing: ListingModel) {
        if (accountService.currentUserId.isEmpty()) {
            _createListingResult.value = Result.failure(Exception("User not authenticated"))
            return
        }

        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                // Ensure the listing has the correct provider ID
                val listingWithProviderId = listing.copy(providerId = accountService.currentUserId)
                
                // Validate the listing
                val validationErrors = listingWithProviderId.validate()
                if (validationErrors.isNotEmpty()) {
                    _createListingResult.value = Result.failure(
                        Exception("Validation failed: ${validationErrors.joinToString(", ")}")
                    )
                    return@launch
                }

                // Save listing in Firestore (with analytics)
                val documentRef = Analytics.measureAndLogSuspend(
                    eventName = Analytics.Events.CREATE_LISTING,
                    additionalParams = mapOf(
                        Analytics.Params.CATEGORY to (listingWithProviderId.category ?: " "),
                        Analytics.Params.SUBCATEGORY to (listingWithProviderId.subcategory ?: " "),
                        Analytics.Params.USER_TYPE to "provider"
                    )
                ) {
                    withContext(Dispatchers.IO) {
                        firestoreRepository.addDocument(
                            ListingModel.COLLECTION_NAME,
                            listingWithProviderId.toMap()
                        )
                    }
                }

                _createListingResult.value = Result.success(documentRef.id)
            } catch (e: Exception) {
                Log.d("createListing error", e.toString())
                _createListingResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Clear the create listing result to prevent re-triggering UI events
    fun clearCreateListingResult() {
        _createListingResult.value = null
    }
} 