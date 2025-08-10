package com.mestero.ui.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mestero.data.models.UserModel
import com.mestero.network.auth.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val accountService: AccountService    // Model layer
) : ViewModel() {

    // Live data used for observing auth state
    private val _authState = MutableLiveData<AuthResult>()
    val authState: LiveData<AuthResult> = _authState

    val currentUserId: String
        get() = accountService.currentUserId

    val hasUser: Boolean
        get() = accountService.hasUser()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            try {
                accountService.signInUsingEmailAndPassword(email, password)
                _authState.value = AuthResult.Success
            } catch (e: Exception) {
                _authState.value = AuthResult.Error(e.message ?: "Sign in error")
            }
        }
    }

    fun registerUsingEmailAndPassword(userModel: UserModel, password: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            try {
                // TODO check case when user already exists - or only first BE call is successful
                accountService.signUpUsingEmailAndPassword(userModel.email, password)

                val uid = accountService.currentUserId
                // If uid exists, sign up was successful, throw exception otherwise
                // TODO moved logic to accountServicem, delete
                if (uid.isEmpty()) throw Exception("UID missing")

                // TODO - if checkbox selected
                saveUserData(userModel)

                _authState.value = AuthResult.Success
            } catch (e: Exception) {
                _authState.value = AuthResult.Error(e.message ?: "Register error")
            }
        }
    }

    private fun saveUserData(user: UserModel) {
        viewModelScope.launch {
            accountService.saveUserData(user)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            accountService.signOut()
        }
    }
}

sealed class AuthResult {
    data object Loading : AuthResult()
    data object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}
