package com.mestero.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ServerTimestamp
import com.mestero.utils.FormatUtils
import com.mestero.constants.FirestoreCollections
import java.util.*

data class ListingModel(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val subcategory: String = "",
    val county: String = "No Preference",
    val specificLocations: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val website: String = "",
    val pricingModel: PricingModel? = null,
    val providerId: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val views: Int = 0,
    val favoritesCount: Int = 0,
    val ratingSum: Int = 0,
    val ratingCount: Int = 0,
    val imageUrls: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val active: Boolean = true
) {
    companion object {
        val COLLECTION_NAME = FirestoreCollections.LISTINGS
        const val MIN_TITLE_LENGTH = 3
        const val MAX_TITLE_LENGTH = 100
        const val MIN_DESCRIPTION_LENGTH = 10
        const val MAX_DESCRIPTION_LENGTH = 1000


        fun fromMap(map: Map<String, Any?>): ListingModel {
            return ListingModel(
                id = map["id"] as? String ?: "",
                title = map["title"] as? String ?: "",
                description = map["description"] as? String ?: "",
                category = map["category"] as? String ?: "",
                subcategory = map["subcategory"] as? String ?: "",
                county = map["county"] as? String ?: "No Preference",
                specificLocations = map["specificLocations"] as? String ?: "",
                phoneNumber = map["phoneNumber"] as? String ?: "",
                email = map["email"] as? String ?: "",
                website = map["website"] as? String ?: "",
                pricingModel = (map["pricingModel"] as? Map<String, Any?>)
                    ?.let { pricingMap ->
                        parsePricingModelFromMap(pricingMap)
                    },
                providerId = map["providerId"] as? String ?: "",
                createdAt = map["createdAt"] as? Timestamp,
                updatedAt = map["updatedAt"] as? Timestamp,
                views = (map["views"] as? Number)?.toInt() ?: 0,
                favoritesCount = (map["favoritesCount"] as? Number)?.toInt() ?: 0,
                ratingSum = parseRatingSumFromMap(map),
                ratingCount = (map["ratingCount"] as? Number)?.toInt() ?: 0,
                imageUrls = (map["imageUrls"] as? List<String>) ?: emptyList(),
                tags = (map["tags"] as? List<String>) ?: emptyList(),
                active = map["active"] as? Boolean ?: true
            )
        }

        private fun parsePricingModelFromMap(pricingMap: Map<String, Any?>): PricingModel {
            val typeString = pricingMap["type"] as? String ?: PricingType.FIXED.name
            val unitString = pricingMap["unit"] as? String ?: PricingUnit.TOTAL.name

            return PricingModel(
                type = PricingType.valueOf(typeString),
                unit = PricingUnit.valueOf(unitString),
                fixedPrice = (pricingMap["fixedPrice"] as? Number)?.toDouble() ?: 0.0,
                minPrice = (pricingMap["minPrice"] as? Number)?.toDouble() ?: 0.0,
                maxPrice = (pricingMap["maxPrice"] as? Number)?.toDouble() ?: 0.0
            )
        }

        private fun parseRatingSumFromMap(map: Map<String, Any?>): Int {
            return (map["ratingSum"] as? Number)?.toInt() ?: 0
        }


        // Create ListingModel from Firestore DocumentSnapshot
        fun fromFirestoreDocument(document: DocumentSnapshot): ListingModel? {
            return try {
                val data = document.data ?: return null
                fromMap(data).copy(id = document.id)
            } catch (e: Exception) {
                null
            }
        }
    }

    // Computed properties declared in the data class for consistency in the Views
    val formattedPrice: String
        get() = pricingModel?.getFormattedPrice() ?: "Contact for price"

    // Calculated rating average (for display purposes)
    val ratingAvg: Double
        get() = if (ratingCount > 0) ratingSum.toDouble() / ratingCount else 0.0

    val formattedRating: String
        get() = FormatUtils.formatRating(ratingAvg)

    val hasContactInfo: Boolean
        get() = phoneNumber.isNotBlank() || email.isNotBlank() || website.isNotBlank()

    val displayLocation: String
        get() = when {
            county == "Online Services" -> "Online Services"
            specificLocations.isNotBlank() -> "$specificLocations, $county"
            county != "No Preference" -> county
            else -> "Location not specified"
        }

    // Check if listing has been created in the last 24 hours // TODO maybe display in home screen
    val isNew: Boolean
        get() = createdAt?.let { timestamp ->
            (Date().time - timestamp.toDate().time) < (24 * 60 * 60 * 1000)
        } ?: false


    fun validate(): List<String> {
        val errorsList = mutableListOf<String>()
        if (title.length < MIN_TITLE_LENGTH || title.length > MAX_TITLE_LENGTH) {
            errorsList.add("Title must be between $MIN_TITLE_LENGTH and $MAX_TITLE_LENGTH characters")
        }

        if (description.length < MIN_DESCRIPTION_LENGTH || description.length > MAX_DESCRIPTION_LENGTH) {
            errorsList.add("Description must be between $MIN_DESCRIPTION_LENGTH and $MAX_DESCRIPTION_LENGTH characters")
        }

        if (category.isBlank()) {
            errorsList.add("Category is required")
        }

        if (subcategory.isBlank()) {
            errorsList.add("Subcategory is required")
        }

        pricingModel?.let { pricing ->
            val pricingValidationErrors = pricing.validate()
            errorsList.addAll(pricingValidationErrors)
        } ?: errorsList.add("Pricing information is required")

        if (email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorsList.add("Please enter a valid email address")
        }

        if (website.isNotBlank() && !android.util.Patterns.WEB_URL.matcher(website).matches()) {
            errorsList.add("Please enter a valid website URL")
        }

        return errorsList
    }

    // Utility functions
    fun incrementViews(): ListingModel {
       return copy(views = views + 1, updatedAt = Timestamp.now())
    }

    fun markListingAsFavorite(isFavorited: Boolean): ListingModel = copy(
        favoritesCount = if (isFavorited) favoritesCount + 1 else favoritesCount - 1,
        updatedAt = Timestamp.now()
    )


    // Serialize for Firestore
    fun toMap(): Map<String, Any?> = mapOf(
        "title" to title,
        "description" to description,
        "category" to category,
        "subcategory" to subcategory,
        "county" to county,
        "specificLocations" to specificLocations,
        "phoneNumber" to phoneNumber,
        "email" to email,
        "website" to website,

        "pricingModel" to pricingModel?.let {
            mapOf(
                "type" to it.type.name,
                "unit" to it.unit.name,
                "fixedPrice" to it.fixedPrice,
                "minPrice" to it.minPrice,
                "maxPrice" to it.maxPrice
            )
        },
        "providerId" to providerId,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "views" to views,
        "favoritesCount" to favoritesCount,
        "ratingSum" to ratingSum,
        "ratingCount" to ratingCount,
        "imageUrls" to imageUrls,
        "tags" to tags,
        "active" to active
    )
} 