package com.mestero.ui.dashboard.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mestero.constants.FirestoreCollections
import com.mestero.data.UserType
import com.mestero.data.models.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _userProfile = MutableLiveData<UserModel>()
    val userProfile: LiveData<UserModel> = _userProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    private var currentUser: UserModel? = null

    fun loadCurrentProfile() {
        val user = auth.currentUser
        if (user == null) {
            _error.value = "User not authenticated"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            try {
                val userDoc = firestore.collection(FirestoreCollections.USERS)
                    .document(user.uid)
                    .get()
                    .await()

                val userModel = if (userDoc.exists()) {
                    val userData = userDoc.data ?: emptyMap()
                    UserModel(
                        email = user.email ?: "",
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
                } else {
                    // Create default user for Firebase Auth
                    UserModel(
                        email = user.email ?: "",
                        firstName = user.displayName?.split(" ")?.firstOrNull() ?: "",
                        lastName = user.displayName?.split(" ")?.drop(1)?.joinToString(" ") ?: "",
                        userType = UserType.CLIENT
                    )
                }

                currentUser = userModel
                _userProfile.value = userModel
            } catch (e: Exception) {
                _error.value = "Failed to load profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveProfile(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        location: String,
        website: String,
        experienceLevel: String,
        skills: List<String>
    ) {
        val user = auth.currentUser
        if (user == null) {
            _error.value = "User not authenticated"
            return
        }

        val current = currentUser
        if (current == null) {
            _error.value = "No current profile data"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val updatedUser = current.copy(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber,
                    location = location,
                    website = website,
                    experienceLevel = experienceLevel,
                    skills = skills
                )

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

                firestore.collection(FirestoreCollections.USERS)
                    .document(user.uid)
                    .set(userMap)
                    .await()

                currentUser = updatedUser
                _saveSuccess.value = true
            } catch (e: Exception) {
                _error.value = "Failed to save profile: ${e.message}"
                _saveSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
} 