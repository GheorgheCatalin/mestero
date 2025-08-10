package com.mestero.network.auth

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.mestero.data.UserType
import com.mestero.data.models.UserModel
import kotlinx.coroutines.flow.Flow

interface AccountService {
    val currentUser: Flow<FirebaseUser?>
    val currentUserId: String
    val userType: LiveData<UserType>

    fun hasUser(): Boolean
    suspend fun signInUsingEmailAndPassword(email: String, password: String)
    suspend fun signUpUsingEmailAndPassword(email: String, password: String)
    suspend fun saveUserData(user: UserModel)
    suspend fun signOut()
    suspend fun deleteAccount()
    suspend fun resetPassword(email: String): Result<Unit>
    fun fetchUserType(
        onResult: (UserType) -> Unit,
        onError: ((Exception) -> Unit)? = null
    )
    fun fetchUserTypeAsString(
        onResult: (String) -> Unit,
        onError: ((Exception) -> Unit)? = null
    )
    fun fetchUserData(
        onResult: (userType: UserType, firstName: String) -> Unit,
        onError: ((Exception) -> Unit)? = null
    )
    fun getCachedUserType(): UserType
    

}