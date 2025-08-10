package com.mestero.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mestero.data.UserType
import com.mestero.network.auth.AccountService
import com.mestero.network.firestore.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized service for user type operations
 * Eliminates code duplication across MainActivity, BookingsFragment, and ViewModels
 */
@Singleton
class UserService @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val accountService: AccountService
) {
    
    private val _userType = MutableLiveData<UserType>()
    val userType: LiveData<UserType> = _userType
    
    /**
     * Fetch user type from Firestore and execute callback with result
     * @param onResult Callback executed on Main thread with UserType
     * @param onError Optional error callback executed on Main thread
     */
    fun fetchUserType(
        onResult: (UserType) -> Unit,
        onError: ((Exception) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userDoc = firestoreRepository.getDocumentData("users", accountService.currentUserId)
                val typeString = if (userDoc?.exists() == true) {
                    userDoc.getString("userType") ?: UserType.CLIENT.name
                } else {
                    UserType.CLIENT.name
                }
                
                val userType = UserType.valueOf(typeString)
                
                withContext(Dispatchers.Main) {
                    _userType.value = userType
                    onResult(userType)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _userType.value = UserType.CLIENT
                    onError?.invoke(e) ?: onResult(UserType.CLIENT)
                }
            }
        }
    }
    
    /**
     * Fetch user type as String (for legacy compatibility)
     */
    fun fetchUserTypeAsString(
        onResult: (String) -> Unit,
        onError: ((Exception) -> Unit)? = null
    ) {
        fetchUserType(
            onResult = { onResult(it.name) },
            onError = onError
        )
    }
    
    /**
     * Get cached user type (returns CLIENT if not cached)
     */
    fun getCachedUserType(): UserType {
        return _userType.value ?: UserType.CLIENT
    }
    
    /**
     * Fetch user data (type and firstName) for ViewModels that need both
     */
    fun fetchUserData(
        onResult: (userType: UserType, firstName: String) -> Unit,
        onError: ((Exception) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userDoc = firestoreRepository.getDocumentData("users", accountService.currentUserId)
                val (userType, firstName) = if (userDoc?.exists() == true) {
                    val typeString = userDoc.getString("userType") ?: UserType.CLIENT.name
                    val name = userDoc.getString("firstName") ?: "User"
                    UserType.valueOf(typeString) to name
                } else {
                    UserType.CLIENT to "User"
                }
                
                withContext(Dispatchers.Main) {
                    _userType.value = userType
                    onResult(userType, firstName)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _userType.value = UserType.CLIENT
                    onError?.invoke(e) ?: onResult(UserType.CLIENT, "User")
                }
            }
        }
    }
} 