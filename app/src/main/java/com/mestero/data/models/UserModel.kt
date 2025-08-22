package com.mestero.data.models

import com.mestero.data.UserType

data class UserModel(
    var email: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var userType: UserType = UserType.CLIENT,
    var phoneNumber: String = "",
    var location: String = "",
    var website: String = "",
    // Provider specific fields
    var skills: List<String> = emptyList(),
    var experienceLevel: String = "",
    var ratingSum: Int = 0, // Sum of all ratings for atomic updates
    var reviewCount: Int = 0
) {
    val displayName: String
        get() = "$firstName $lastName".trim().ifEmpty { "User" }
        
    val hasPhoneNumber: Boolean
        get() = phoneNumber.isNotEmpty()
        
    val hasLocation: Boolean
        get() = location.isNotEmpty()
        
    val hasWebsite: Boolean
        get() = website.isNotEmpty()
        
    val hasExperience: Boolean
        get() = experienceLevel.isNotEmpty()
        
    val hasSkills: Boolean
        get() = skills.isNotEmpty()
    

    val rating: Float
        get() = if (reviewCount > 0) ratingSum.toFloat() / reviewCount else 0.0f

}