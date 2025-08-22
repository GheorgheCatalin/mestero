package com.mestero.ui.dashboard.bookings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.mestero.data.models.BookingRequestModel
import com.mestero.data.models.RequestStatus
import com.mestero.network.auth.AccountService
import com.mestero.network.firestore.FirestoreQueryFilterType
import com.mestero.network.firestore.FirestoreRepository
import com.mestero.network.firestore.FirestoreQueryFilter
import com.mestero.network.firestore.FirestoreQueryParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BookingsViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val accountService: AccountService
) : ViewModel() {

    private val _providerBookingsState = MutableLiveData<BookingsUiState>()
    val providerBookingsState: LiveData<BookingsUiState> = _providerBookingsState

    private val _updateResult = MutableLiveData<Result<String>>()
    val updateResult: LiveData<Result<String>> = _updateResult

    init {
        loadBookings()
    }

    private fun loadBookings() {
        val currentUserId = accountService.currentUserId
        if (currentUserId.isEmpty()) {
            _providerBookingsState.value = BookingsUiState.Empty
            return
        }

        _providerBookingsState.value = BookingsUiState.Loading

        viewModelScope.launch {
            try {
                val queryParams = FirestoreQueryParams(
                    filters = listOf(
                        FirestoreQueryFilter(
                            field = "providerId",
                            value = currentUserId,
                            type = FirestoreQueryFilterType.EQUAL_TO
//                        ),
                            //TODO can remove handled by in memory filtering
//                        QueryFilter(
//                            field = "hiddenForProvider",
//                            value = false,
//                            type = FilterType.EQUAL_TO
                        )
                    ),
                    orderBy = "createdAt",
                    orderDirection = Query.Direction.DESCENDING
                )

                val querySnapshot = firestoreRepository.queryDocuments(
                    BookingRequestModel.COLLECTION_NAME, 
                    queryParams
                )
                
                val bookings = querySnapshot.documents.mapNotNull { doc ->
                    try {
                        BookingRequestModel.fromMap(doc.data ?: emptyMap()).copy(id = doc.id)
                    } catch (e: Exception) {
                        Log.w("BookingsViewModel", "Failed to parse booking: ${e.message}")
                        null
                    }
                }.filter {
                    !it.hiddenForProvider
                }

                if (bookings.isEmpty()) {
                    _providerBookingsState.value = BookingsUiState.Empty
                } else {
                    _providerBookingsState.value = BookingsUiState.Success(bookings)
                }

            } catch (e: Exception) {
                Log.e("BookingsViewModel", "Failed to load bookings", e)
                _providerBookingsState.value = BookingsUiState.Error("Failed to load bookings: ${e.message}")
            }
        }
    }

    private fun refreshBookings() {
        loadBookings()
    }

    fun acceptBooking(bookingId: String, providerNotes: String = "") {
        updateBookingStatus(bookingId, RequestStatus.ACCEPTED, providerNotes)

    }

    fun rejectBooking(bookingId: String, providerNotes: String = "") {
        updateBookingStatus(bookingId, RequestStatus.REJECTED, providerNotes)
    }

    fun completeBooking(bookingId: String) {
        updateBookingStatus(
            bookingId,
            RequestStatus.COMPLETED,
            "",
            Timestamp.now()
        )
    }

    private fun updateBookingStatus(
        bookingId: String, 
        newStatus: RequestStatus, 
        providerNotes: String = "",
        completedAt: Timestamp? = null
    ) {
        viewModelScope.launch {
            try {
                val updateData = mutableMapOf<String, Any>(
                    "status" to newStatus.name,
                    "updatedAt" to Timestamp.now()
                )
                
                if (providerNotes.isNotBlank()) {
                    updateData["providerNotes"] = providerNotes
                }
                
                if (completedAt != null) {
                    updateData["completedAt"] = completedAt
                }

                firestoreRepository.updateDocument(
                    BookingRequestModel.COLLECTION_NAME,
                    bookingId,
                    updateData
                )


                val statusText = when (newStatus) {
                    RequestStatus.ACCEPTED -> "accepted"
                    RequestStatus.REJECTED -> "rejected"
                    RequestStatus.COMPLETED -> "marked as completed"
                    else -> "updated"
                }

                _updateResult.value = Result.success("Booking $statusText successfully!")
                loadBookings() // Reload page with new data

            } catch (e: Exception) {
                Log.e("BookingsViewModel", "Failed to update booking status", e)
                _updateResult.value = Result.failure(e)
            }
        }
    }

    fun hideBooking(bookingId: String) {
        viewModelScope.launch {
            try {
                val updateData = mapOf(
                    "hiddenForProvider" to true,
                    "updatedAt" to Timestamp.now()
                )

                firestoreRepository.updateDocument(
                    BookingRequestModel.COLLECTION_NAME,
                    bookingId,
                    updateData
                )

                _updateResult.value = Result.success("Booking hidden successfully!")
                refreshBookings() // Reload page with new data

            } catch (e: Exception) {
                Log.e("BookingsViewModel", "Failed to hide booking", e)
                _updateResult.value = Result.failure(e)
            }
        }
    }
}

sealed interface BookingsUiState {
    data object Loading : BookingsUiState
    data class Success(val bookings: List<BookingRequestModel>) : BookingsUiState
    data object Empty : BookingsUiState
    data class Error(val message: String) : BookingsUiState
}