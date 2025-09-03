package com.mestero.ui.dashboard.listingDetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.mestero.constants.FirestoreCollections
import com.mestero.data.UserType
import com.mestero.data.models.BookingRequestModel
import com.mestero.data.models.ListingModel
import com.mestero.data.models.RequestStatus
import com.mestero.data.models.UserModel
import com.mestero.network.auth.AccountService
import com.mestero.network.firestore.FirestoreQueryFilterType
import com.mestero.network.firestore.FirestoreRepository
import com.mestero.network.firestore.FirestoreQueryFilter
import com.mestero.network.firestore.FirestoreQueryParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ListingDetailViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val accountService: AccountService
) : ViewModel() {

    private val _listingDetailState = MutableLiveData<ListingDetailUiState>()
    val listingDetailState: LiveData<ListingDetailUiState> = _listingDetailState
    
    private val _bookingResult = MutableLiveData<Result<String>>()
    val bookingResult: LiveData<Result<String>> = _bookingResult

    fun loadListingDetail(listingId: String) {
        if (listingId.isEmpty()) {
            _listingDetailState.value = ListingDetailUiState.Error("Invalid listing ID")
            return
        }

        _listingDetailState.value = ListingDetailUiState.Loading

        viewModelScope.launch {
            try {
                val listing = getListing(listingId) ?: return@launch

                val provider = getProvider(listing.providerId)

                incrementViewCount(listingId, listing.views)
                
                _listingDetailState.value = ListingDetailUiState.Success(listing, provider)
            } catch (e: Exception) {
                Log.e("ListingDetailViewModel", "Error loading listing detail", e)
                _listingDetailState.value = ListingDetailUiState.Error(
                    "Failed to load listing: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    private suspend fun getListing(listingId: String): ListingModel? {
        val listingDoc = firestoreRepository.getDocumentData(
            collection = ListingModel.COLLECTION_NAME,
            documentId = listingId
        )

        if (listingDoc?.exists() != true) {
            _listingDetailState.value = ListingDetailUiState.Error("Listing not found")
            return null
        }

        val listing = listingDoc.toObject(ListingModel::class.java)?.copy(id = listingDoc.id)
        if (listing == null) {
            _listingDetailState.value = ListingDetailUiState.Error("Failed to load listing data")
            return null
        }

        return listing
    }

    private suspend fun getProvider(providerId: String): UserModel? {
        if (providerId.isEmpty()) return null

        return try {
            val providerDoc = firestoreRepository.getDocumentData(
                collection = FirestoreCollections.USERS,
                documentId = providerId
            )
            
            if (providerDoc?.exists() == true) {
                val userData = providerDoc.data ?: emptyMap()
                UserModel(
                    email = userData["email"] as? String ?: "",
                    firstName = userData["firstName"] as? String ?: "",
                    lastName = userData["lastName"] as? String ?: "",
                    userType = parseUserType(userData["userType"] as? String),
                    phoneNumber = userData["phoneNumber"] as? String ?: "",
                    location = userData["location"] as? String ?: "",
                    website = userData["website"] as? String ?: "",
                    skills = (userData["skills"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                    experienceLevel = userData["experienceLevel"] as? String ?: "",
                    ratingSum = (userData["ratingSum"] as? Number)?.toInt() ?: 0,
                    reviewCount = (userData["reviewCount"] as? Number)?.toInt() ?: 0
                )
            } else {
                null
            }
        } catch (e: Exception) {
            // Non-critical, can continue
            Log.w("ListingDetailViewModel", "Failed to load provider info", e)
            null
        }
    }

    private fun parseUserType(userTypeString: String?): UserType {
        return try {
            UserType.valueOf(userTypeString ?: UserType.CLIENT.name)
        } catch (e: IllegalArgumentException) {
            UserType.CLIENT
        }
    }

    private suspend fun incrementViewCount(listingId: String, currentViews: Int) {
        try {
            firestoreRepository.updateDocument(
                collection = ListingModel.COLLECTION_NAME,
                documentId = listingId,
                data = mapOf("views" to currentViews + 1)
            )
        } catch (e: Exception) {
            // Non-critical, can continue
            Log.w("ListingDetailViewModel", "Failed to increment view count", e)
        }
    }
    
    fun createBookingRequest(
        listing: ListingModel,
        notes: String
    ) {
        val currentUserId = accountService.currentUserId
        if (currentUserId.isEmpty()) {
            _bookingResult.value = Result.failure(Exception("User not authenticated"))
            return
        }
        
        if (currentUserId == listing.providerId) {
            _bookingResult.value = Result.failure(Exception("You cannot book your own service"))
            return
        }

        viewModelScope.launch {
            try {
                // Check for existing requests first
                if (hasExistingPendingRequest(listing.id, currentUserId)) {
                    _bookingResult.value = Result.failure(
                        Exception("You already have a pending booking request for this service. Please wait for the provider's response.")
                    )
                    return@launch
                }

                // Get client info
                val clientData = getClientDetails(currentUserId)
                // Get provider info from current UI state
                val providerData = extractProviderData()

                val bookingRequest = buildBookingRequest(listing, notes, currentUserId, clientData, providerData)
                val requestId = firestoreRepository.addDocument(
                    BookingRequestModel.COLLECTION_NAME,
                    bookingRequest
                )

                _bookingResult.value = Result.success("Booking request sent successfully!")
                Log.d("ListingDetailViewModel", "Booking request created with ID: $requestId")
                
            } catch (e: Exception) {
                Log.e("ListingDetailViewModel", "Failed to create booking request", e)
                _bookingResult.value = Result.failure(e)
            }
        }
    }

    private suspend fun hasExistingPendingRequest(listingId: String, clientId: String): Boolean {
        val existingRequestQuery = FirestoreQueryParams(
            filters = listOf(
                FirestoreQueryFilter("listingId", listingId, FirestoreQueryFilterType.EQUAL_TO),
                FirestoreQueryFilter("clientId", clientId, FirestoreQueryFilterType.EQUAL_TO),
                FirestoreQueryFilter("status", RequestStatus.PENDING.name, FirestoreQueryFilterType.EQUAL_TO)
            )
        )
        
        val existingRequests = firestoreRepository.queryDocuments(
            BookingRequestModel.COLLECTION_NAME,
            existingRequestQuery
        )
        
        return !existingRequests.isEmpty
    }

    private suspend fun getClientDetails(currentUserId: String): Triple<String, String, String> {
        val userDoc = firestoreRepository.getDocumentData(FirestoreCollections.USERS, currentUserId)
        val userData = userDoc?.data ?: emptyMap()
        
        val clientName = "${userData["firstName"] ?: ""} ${userData["lastName"] ?: ""}".trim().ifEmpty { "User" }
        val clientEmail = userData["email"] as? String ?: ""
        val clientPhone = userData["phoneNumber"] as? String ?: ""
        
        return Triple(clientName, clientEmail, clientPhone)
    }

    private fun extractProviderData(): Triple<String, String, String> {
        val currentState = _listingDetailState.value
        val provider = if (currentState is ListingDetailUiState.Success) {
            currentState.provider
        } else {
            null
        }
        
        val providerName = provider?.let { 
            "${it.firstName} ${it.lastName}"
                .trim()
                .ifEmpty { "Provider" }
        } ?: "Provider"
        
        val providerEmail = provider?.email ?: ""
        val providerPhone = provider?.phoneNumber ?: ""
        
        return Triple(providerName, providerEmail, providerPhone)
    }

    private fun buildBookingRequest(
        listing: ListingModel,
        notes: String,
        currentUserId: String,
        clientData: Triple<String, String, String>,
        providerData: Triple<String, String, String>
    ): BookingRequestModel {
        val (clientName, clientEmail, clientPhone) = clientData
        val (providerName, providerEmail, providerPhone) = providerData
        
        return BookingRequestModel(
            listingId = listing.id,
            listingTitle = listing.title,
            providerId = listing.providerId,
            providerName = providerName,
            providerEmail = providerEmail,
            providerPhone = providerPhone,
            clientId = currentUserId,
            clientName = clientName,
            clientEmail = clientEmail,
            clientPhone = clientPhone,
            notes = notes,
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        )
    }
}

sealed interface ListingDetailUiState {
    data object Loading : ListingDetailUiState
    data class Success(
        val listing: ListingModel,
        val provider: UserModel?
    ) : ListingDetailUiState
    data class Error(val message: String) : ListingDetailUiState
}