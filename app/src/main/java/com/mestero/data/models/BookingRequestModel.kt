package com.mestero.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

enum class RequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    COMPLETED
}

data class BookingRequestModel(
    @DocumentId
    val id: String = "",
    val listingId: String = "",
    val listingTitle: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val providerEmail: String = "",
    val providerPhone: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val clientEmail: String = "",
    val clientPhone: String = "",
    val status: RequestStatus = RequestStatus.PENDING,
    val notes: String = "", // Client's additional notes/requirements
    val providerNotes: String = "", // Provider's response notes
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val completedAt: Timestamp? = null,
    val hiddenForClient: Boolean = false,
    val hiddenForProvider: Boolean = false
) {
    companion object {
        const val COLLECTION_NAME = "booking_requests"

        fun fromMap(map: Map<String, Any?>): BookingRequestModel {
            return BookingRequestModel(
                id = map["id"] as? String ?: "",
                listingId = map["listingId"] as? String ?: "",
                listingTitle = map["listingTitle"] as? String ?: "",
                providerId = map["providerId"] as? String ?: "",
                providerName = map["providerName"] as? String ?: "",
                providerEmail = map["providerEmail"] as? String ?: "",
                providerPhone = map["providerPhone"] as? String ?: "",
                clientId = map["clientId"] as? String ?: "",
                clientName = map["clientName"] as? String ?: "",
                clientEmail = map["clientEmail"] as? String ?: "",
                clientPhone = map["clientPhone"] as? String ?: "",
                status = try {
                    RequestStatus.valueOf(map["status"] as? String ?: RequestStatus.PENDING.name)
                } catch (e: IllegalArgumentException) {
                    RequestStatus.PENDING
                },
                notes = map["notes"] as? String ?: "",
                providerNotes = map["providerNotes"] as? String ?: "",
                createdAt = map["createdAt"] as? Timestamp,
                updatedAt = map["updatedAt"] as? Timestamp,
                completedAt = map["completedAt"] as? Timestamp,
                hiddenForClient = map["hiddenForClient"] as? Boolean ?: false,
                hiddenForProvider = map["hiddenForProvider"] as? Boolean ?: false
            )
        }
    }


    val statusDisplayText: String
        get() = when (status) {
            RequestStatus.PENDING -> "Pending Response"
            RequestStatus.ACCEPTED -> "Accepted"
            RequestStatus.REJECTED -> "Rejected"
            RequestStatus.COMPLETED -> "Completed"
        }
    
    val statusColor: Int
        get() = when (status) {
            RequestStatus.PENDING -> android.R.color.holo_orange_light
            RequestStatus.ACCEPTED -> android.R.color.holo_green_light
            RequestStatus.REJECTED -> android.R.color.holo_red_light
            RequestStatus.COMPLETED -> android.R.color.holo_blue_light
        }

    fun statusColor() : Int {
        return  when (status) {
            RequestStatus.PENDING -> android.R.color.holo_orange_light
            RequestStatus.ACCEPTED -> android.R.color.holo_green_light
            RequestStatus.REJECTED -> android.R.color.holo_red_light
            RequestStatus.COMPLETED -> android.R.color.holo_blue_light
        }
    }

    // Check if request can be marked as completed
    val canBeCompleted: Boolean
        get() = status == RequestStatus.ACCEPTED
    
    // Check if request can be accepted/rejected
    val canBeResponded: Boolean
        get() = status == RequestStatus.PENDING
} 