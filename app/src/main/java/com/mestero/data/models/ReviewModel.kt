package com.mestero.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.mestero.utils.FormatUtils
import com.mestero.constants.FirestoreCollections

data class ReviewModel(
    @DocumentId
    val id: String = "",
    val bookingId: String = "",
    val listingId: String = "",
    val listingTitle: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val serviceRating: Int? = null,
    val serviceComment: String = "",
    val providerRating: Int? = null,
    val providerComment: String = "",
    val isAnonymous: Boolean = false,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    // Moderation fields TODO add impl later or delete
    val isReported: Boolean = false,
    val isHidden: Boolean = false,
    val moderationNotes: String = ""
) {
    companion object {
        val COLLECTION_NAME = FirestoreCollections.REVIEWS
        const val MIN_RATING = 1
        const val MAX_RATING = 5
        const val MIN_COMMENT_LENGTH = 5
        const val MAX_COMMENT_LENGTH = 500

        fun fromMap(map: Map<String, Any?>): ReviewModel {
            return ReviewModel(
                id = map["id"] as? String ?: "",
                bookingId = map["bookingId"] as? String ?: "",
                listingId = map["listingId"] as? String ?: "",
                listingTitle = map["listingTitle"] as? String ?: "",
                providerId = map["providerId"] as? String ?: "",
                providerName = map["providerName"] as? String ?: "",
                clientId = map["clientId"] as? String ?: "",
                clientName = map["clientName"] as? String ?: "",
                serviceRating = (map["serviceRating"] as? Number)?.toInt(),
                serviceComment = map["serviceComment"] as? String ?: "",
                providerRating = (map["providerRating"] as? Number)?.toInt(),
                providerComment = map["providerComment"] as? String ?: "",
                isAnonymous = map["isAnonymous"] as? Boolean ?: false,
                createdAt = map["createdAt"] as? Timestamp,
                updatedAt = map["updatedAt"] as? Timestamp,
                isReported = map["isReported"] as? Boolean ?: false,
                isHidden = map["isHidden"] as? Boolean ?: false,
                moderationNotes = map["moderationNotes"] as? String ?: ""
            )
        }
    }

    val hasServiceReview: Boolean
        get() = serviceRating != null && serviceComment.length >= MIN_COMMENT_LENGTH

    val hasProviderReview: Boolean
        get() = providerRating != null && providerComment.length >= MIN_COMMENT_LENGTH

    val hasAnyReview: Boolean
        get() = hasServiceReview || hasProviderReview

    // Display properties - TODO could later implement to service / provider
    val displayClientName: String
        get() = if (isAnonymous) "Anonymous" else clientName.ifBlank { "Anonymous" }

    val serviceRatingStars: String
        get() = serviceRating?.let { "★".repeat(it) + "☆".repeat(5 - it) } ?: ""

    val providerRatingStars: String
        get() = providerRating?.let { "★".repeat(it) + "☆".repeat(5 - it) } ?: ""

    val formattedDate: String
        get() = FormatUtils.formatRelativeDate(createdAt)

    fun validate(): List<String> {
        val errorsList = mutableListOf<String>()

        if (!hasAnyReview) {
            errorsList.add("Please provide at least one review (service or provider)")
        }

        serviceRating?.let { rating ->
            if (rating < MIN_RATING || rating > MAX_RATING) {
                errorsList.add("Service rating must be between $MIN_RATING and $MAX_RATING stars")
            }
        }

        if (serviceRating != null && serviceComment.length < MIN_COMMENT_LENGTH) {
            errorsList.add("Service review comment must be at least $MIN_COMMENT_LENGTH characters")
        }
        if (serviceComment.length > MAX_COMMENT_LENGTH) {
            errorsList.add("Service review comment cannot exceed $MAX_COMMENT_LENGTH characters")
        }

        // TODO - not possible to occur (can keep for additional bug proofing/defensive programming
        providerRating?.let { rating ->
            if (rating < MIN_RATING || rating > MAX_RATING) {
                errorsList.add("Provider rating must be between $MIN_RATING and $MAX_RATING stars")
            }
        }

        // Validate provider review if provided // TODO remove ? make comm optional
        if (providerRating != null && providerComment.length < MIN_COMMENT_LENGTH) {
            errorsList.add("Provider review comment must be at least $MIN_COMMENT_LENGTH characters")
        }

        if (providerComment.length > MAX_COMMENT_LENGTH) {
            errorsList.add("Provider review comment cannot exceed $MAX_COMMENT_LENGTH characters")
        }

        // TODO - not possible to occur (can keep for additional bug proofing/defensive programming / Data Integrity Protection
        if (bookingId.isBlank()) {
            errorsList.add("Error fetching booking")
        }

        if (providerId.isBlank()) {
            errorsList.add("Error fetching provider")
        }

        if (clientId.isBlank()) {
            errorsList.add("Error fetching client")
        }

        return errorsList
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "bookingId" to bookingId,
        "listingId" to listingId,
        "listingTitle" to listingTitle,
        "providerId" to providerId,
        "providerName" to providerName,
        "clientId" to clientId,
        "clientName" to clientName,
        "serviceRating" to serviceRating,
        "serviceComment" to serviceComment,
        "providerRating" to providerRating,
        "providerComment" to providerComment,
        "isAnonymous" to isAnonymous,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "isReported" to isReported,
        "isHidden" to isHidden,
        "moderationNotes" to moderationNotes
    )
} 