package com.mestero.ui.dashboard.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.mestero.data.UserType
import com.mestero.data.models.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _userProfile = MutableLiveData<UserModel>()
    val userProfile: LiveData<UserModel> = _userProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _error.value = "User not authenticated"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userDoc = firestore.collection("users")
                    .document(currentUser.uid)
                    .get()
                    .await()

                if (userDoc.exists()) {
                    val userData = userDoc.data ?: emptyMap()
                    val userModel = UserModel(
                        email = currentUser.email ?: "",
                        firstName = userData["firstName"] as? String ?: "",
                        lastName = userData["lastName"] as? String ?: "",
                        userType = try {
                            UserType.valueOf(userData["userType"] as? String ?: UserType.CLIENT.name)
                        } catch (e: IllegalArgumentException) {
                            UserType.CLIENT
                        },
                        phoneNumber = userData["phoneNumber"] as? String ?: "",
                        location = userData["location"] as? String ?: "",
                        website = userData["website"] as? String ?: "",
                        skills = (userData["skills"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                        experienceLevel = userData["experienceLevel"] as? String ?: "",
                        ratingSum = (userData["ratingSum"] as? Number)?.toInt() ?: 0,
                        reviewCount = (userData["reviewCount"] as? Number)?.toInt() ?: 0
                    )
                    _userProfile.value = userModel
                } else {
                    // No Firestore document -> use Firebase Auth data as fallback
                    _userProfile.value = createFallbackUser(currentUser)
                }
            } catch (e: Exception) {
                _error.value = "Failed to load profile: ${e.message}"
                // Error loading -> use Firebase Auth data as fallback
                _userProfile.value = createFallbackUser(currentUser)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(updatedUser: UserModel) {
        val currentUser = auth.currentUser ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userMap = mapOf(
                    "firstName" to updatedUser.firstName,
                    "lastName" to updatedUser.lastName,
                    "userType" to updatedUser.userType.name,
                    "phoneNumber" to updatedUser.phoneNumber,
                    "location" to updatedUser.location,
                    "website" to updatedUser.website,
                    "skills" to updatedUser.skills,
                    "experienceLevel" to updatedUser.experienceLevel,
                    "ratingSum" to updatedUser.ratingSum,
                    "reviewCount" to updatedUser.reviewCount
                )

                firestore.collection("users")
                    .document(currentUser.uid)
                    .set(userMap)
                    .await()

                _userProfile.value = updatedUser
            } catch (e: Exception) {
                _error.value = "Failed to update profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun createFallbackUser(firebaseUser: FirebaseUser): UserModel {
        return UserModel(
            email = firebaseUser.email ?: "",
            firstName = firebaseUser.displayName?.split(" ")?.firstOrNull() ?: "User",
            lastName = firebaseUser.displayName?.split(" ")?.drop(1)?.joinToString(" ") ?: "",
            userType = UserType.CLIENT
        )
    }
} 