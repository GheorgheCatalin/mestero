package com.mestero.ui.dashboard.bookings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mestero.data.models.BookingRequestModel
import com.mestero.data.models.ReviewModel
import com.mestero.network.auth.AccountService
import com.mestero.network.firestore.FirestoreQueryFilterType
import com.mestero.network.firestore.FirestoreRepository
import com.mestero.network.firestore.FirestoreQueryFilter
import com.mestero.network.firestore.FirestoreQueryParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.mestero.constants.FirestoreCollections


@HiltViewModel
class ClientBookingsViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val accountService: AccountService
) : ViewModel() {

    private val _clientBookingsState = MutableLiveData<ClientBookingsUiState>()
    val clientBookingsState: LiveData<ClientBookingsUiState> = _clientBookingsState

    private val _contactAction = MutableLiveData<Pair<String, String>?>()
    val contactAction: LiveData<Pair<String, String>?> = _contactAction

    private val _reviewAction = MutableLiveData<BookingRequestModel?>()
    val reviewAction: LiveData<BookingRequestModel?> = _reviewAction

    private val _updateResult = MutableLiveData<Result<String>>()
    val updateResult: LiveData<Result<String>> = _updateResult

    init {
        loadClientBookings()
    }

    private fun loadClientBookings() {
        val currentUserId = accountService.currentUserId
        if (currentUserId.isEmpty()) {
            _clientBookingsState.value = ClientBookingsUiState.Empty
            return
        }

        _clientBookingsState.value = ClientBookingsUiState.Loading

        viewModelScope.launch {
            try {
                val queryParams = FirestoreQueryParams(
                    filters = listOf(
                        FirestoreQueryFilter(
                            field = "clientId",
                            value = currentUserId,
                            type = FirestoreQueryFilterType.EQUAL_TO
                        )
                        // Filter hidden bookings in memory instead of db
                        // QueryFilter(
                        //     field = "hiddenForClient",
                        //     value = false,
                        //     type = FilterType.EQUAL_TO
                        // )
                    ),
                    orderBy = "createdAt",
                    orderDirection = com.google.firebase.firestore.Query.Direction.DESCENDING
                )

                val querySnapshot = firestoreRepository.queryDocuments(
                    BookingRequestModel.COLLECTION_NAME, 
                    queryParams
                )
                
                val bookings = querySnapshot.documents.mapNotNull { doc ->
                    try {
                        BookingRequestModel.fromMap(doc.data ?: emptyMap()).copy(id = doc.id)
                    } catch (e: Exception) {
                        Log.w("ClientBookingsViewModel", "Failed to parse bookings: ${e.message}")
                        null
                    }
                }.filter {
                    // Filter out hidden bookings in order to show only visible bookings to the client
                    !it.hiddenForClient
                }

                if (bookings.isEmpty()) {
                    _clientBookingsState.value = ClientBookingsUiState.Empty
                } else {
                    _clientBookingsState.value = ClientBookingsUiState.Success(bookings)
                }

            } catch (e: Exception) {
                Log.e("ClientBookingsViewModel", "Failed to load client bookings", e)
                _clientBookingsState.value = ClientBookingsUiState.Error("Failed to load your bookings: ${e.message}")
            }
        }
    }

    fun submitReview(review: ReviewModel) {
        viewModelScope.launch {
            try {
                // Check if review is valid before uploading
                val validationErrors = review.validate()
                if (validationErrors.isNotEmpty()) {
                    _updateResult.value = Result.failure(Exception(validationErrors.joinToString(". ")))
                    return@launch
                }

                // Check for already existing reviews for this booking ID
                val existingReviewQuery = FirestoreQueryParams(
                    filters = listOf(
                        FirestoreQueryFilter(
                            field = "bookingId",
                            value = review.bookingId,
                            type = FirestoreQueryFilterType.EQUAL_TO
                        )
                    )
                )
                val existingReviews = firestoreRepository.queryDocuments(
                    ReviewModel.COLLECTION_NAME,
                    existingReviewQuery
                )

                if (!existingReviews.isEmpty) {
                    _updateResult.value =
                        Result.failure(Exception("You have already reviewed this booking"))
                    return@launch
                }

                // Submit review with atomic rating updates
                submitReviewWithRatingUpdates(review)

                _updateResult.value = Result.success("Review submitted successfully!")
                // Refresh the page
                loadClientBookings()

            } catch (e: Exception) {
                Log.e("ClientBookingsViewModel", "Failed to submit review", e)

                _updateResult.value = Result.failure(e)
            }
        }
    }


    // Submits reviews using an atomic Firestore transaction
    // Makes sure all calls succeed together to avoid partial or inconsistent updates
    private suspend fun submitReviewWithRatingUpdates(review: ReviewModel) {
        // If one call fails the whole transaction will be annulled
        firestoreRepository.runTransaction { transaction ->
            // Add review document
            val reviewRef = firestoreRepository.getCollectionReference(ReviewModel.COLLECTION_NAME).document()
            transaction.set(reviewRef, review.toMap())

            //  Update provider rating (if provider review exists)
            review.providerRating?.let { providerRating ->
                val providerRef = firestoreRepository.getCollectionReference(FirestoreCollections.USERS).document(review.providerId)
                transaction.update(providerRef, mapOf(
                    "ratingSum" to FieldValue.increment(providerRating.toLong()),
                    "reviewCount" to FieldValue.increment(1)
                ))
            }

            // Update listing rating (if service review exists)
            review.serviceRating?.let { serviceRating ->
                val listingRef = firestoreRepository.getCollectionReference(FirestoreCollections.LISTINGS).document(review.listingId)
                transaction.update(listingRef, mapOf(
                    "ratingSum" to FieldValue.increment(serviceRating.toLong()),
                    "ratingCount" to FieldValue.increment(1)
                ))
            }
        }
    }

    fun hideBooking(bookingId: String) {
        viewModelScope.launch {
            try {
                val updateData = mapOf(
                    "hiddenForClient" to true,
                    "updatedAt" to Timestamp.now()
                )

                firestoreRepository.updateDocument(
                    BookingRequestModel.COLLECTION_NAME,
                    bookingId,
                    updateData
                )

                _updateResult.value = Result.success("Booking hidden successfully!")
                // Refresh the page
                loadClientBookings()

            } catch (e: Exception) {
                Log.e("ClientBookingsViewModel", "Failed to hide booking", e)
                _updateResult.value = Result.failure(e)
            }
        }
    }

    fun refreshBookings() {
        loadClientBookings()
    }

    fun handleContactClick(contactInfo: String, type: String) {
        _contactAction.value = Pair(contactInfo, type)
    }

    fun handleReviewClick(booking: BookingRequestModel) {
        _reviewAction.value = booking
    }

    // Clear action to prevent re-triggering on config changes (rotation, etc.)
    fun clearReviewAction() {
        _reviewAction.value = null
    }

    fun clearContactAction() {
        _contactAction.value = null
    }
}


sealed interface ClientBookingsUiState {
    data object Loading : ClientBookingsUiState
    data class Success(val bookings: List<BookingRequestModel>) : ClientBookingsUiState
    data object Empty : ClientBookingsUiState
    data class Error(val message: String) : ClientBookingsUiState
}