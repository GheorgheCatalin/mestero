package com.mestero.network.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuthSettings
import com.google.firebase.firestore.firestore
import com.mestero.data.UserType
import com.mestero.data.models.UserModel
import com.mestero.network.firestore.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


//interface AccountService {
//    val currentUser: Flow<FirebaseUser?>
//    val currentUserId: String
//    fun hasUser(): Boolean
//    suspend fun signInUsingEmailAndPassword(email: String, password: String)
//    suspend fun signUpUsingEmailAndPassword(email: String, password: String)
//    suspend fun signOut()
//    suspend fun deleteAccount()
//}

@Singleton
class AccountServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestoreRepository: FirestoreRepository
) : AccountService {

    private val _userType = MutableLiveData<UserType>()
    override val userType: LiveData<UserType> = _userType

    override val currentUser: Flow<FirebaseUser?>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                trySend(auth.currentUser)
            }

            firebaseAuth.addAuthStateListener(listener)
            awaitClose {
                firebaseAuth.removeAuthStateListener(listener)
            }
        }

    override val currentUserId: String
        get() = firebaseAuth.currentUser?.uid.orEmpty()

    override fun hasUser(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun signInUsingEmailAndPassword(email: String, password: String) {
        // TODO implement loading for async = await()
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun signUpUsingEmailAndPassword(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun saveUserData(user: UserModel) {
        val uid = firebaseAuth.currentUser?.uid
            ?: throw Exception("User not logged in")

        Firebase.firestore.collection("users")
            .document(uid)
            .set(user)
            .await()
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun deleteAccount() {
        firebaseAuth.currentUser?.delete()?.await()
    }

//    override suspend fun signIn(email: String, password: String): Result<FirebaseUser> = runCatching {
//        firebaseAuth.signInWithEmailAndPassword(email, password).await().user
//            ?: throw IllegalStateException("User is null after sign in")
//    }
//
//    override suspend fun signUp(email: String, password: String): Result<FirebaseUser> = runCatching {
//        firebaseAuth.createUserWithEmailAndPassword(email, password).await().user
//            ?: throw IllegalStateException("User is null after sign up")
//    }

    override suspend fun resetPassword(email: String): Result<Unit> = runCatching {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    override fun fetchUserType(
        onResult: (UserType) -> Unit,
        onError: ((Exception) -> Unit)?
    ) {
        // Use background thread for database work
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userDoc = firestoreRepository.getDocumentData("users", currentUserId)
                val typeString = if (userDoc?.exists() == true) {
                    userDoc.getString("userType") ?: UserType.CLIENT.name
                } else {
                    UserType.CLIENT.name
                }
                
                val userType = UserType.valueOf(typeString)

                // Use main thread for the UI updates - crash fix
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
    
    override fun fetchUserTypeAsString(
        onResult: (String) -> Unit,
        onError: ((Exception) -> Unit)?
    ) {
        fetchUserType(
            onResult = { onResult(it.name) },
            onError = onError
        )
    }
    
    override fun fetchUserData(
        onResult: (userType: UserType, firstName: String) -> Unit,
        onError: ((Exception) -> Unit)?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userDoc = firestoreRepository.getDocumentData("users", currentUserId)
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
    
    override fun getCachedUserType(): UserType {
        return _userType.value ?: UserType.CLIENT
    }
    

}
