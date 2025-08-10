package com.mestero.network.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Transaction

interface FirestoreRepository {
    suspend fun getDocument(collection: String, documentId: String): DocumentReference
    suspend fun getDocuments(collection: String): QuerySnapshot
    suspend fun addDocument(collection: String, data: Any): DocumentReference
    suspend fun updateDocument(collection: String, documentId: String, data: Any)
    suspend fun deleteDocument(collection: String, documentId: String)
    suspend fun getDocumentData(collection: String, documentId: String): DocumentSnapshot?
    suspend fun queryByField(collection: String, field: String, value: Any): QuerySnapshot
    suspend fun queryByFieldWithLimit(collection: String, field: String, value: Any, limit: Long): QuerySnapshot
    suspend fun queryByFieldWithOrder(collection: String, field: String, value: Any, orderBy: String, orderDirection: Query.Direction = Query.Direction.ASCENDING): QuerySnapshot
    suspend fun queryDocuments(collection: String, params: FirestoreQueryParams): QuerySnapshot
    suspend fun runTransaction(updateFunction: (Transaction) -> Unit)
    fun getCollectionReference(collection: String): CollectionReference
}


enum class FirestoreQueryFilterType {
    EQUAL_TO,
    NOT_EQUAL_TO,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL_TO,
    LESS_THAN,
    LESS_THAN_OR_EQUAL_TO,
    ARRAY_CONTAINS,
    ARRAY_CONTAINS_ANY,
    IN,
    NOT_IN
}

data class FirestoreQueryFilter(
    val field: String,
    val value: Any,
    val type: FirestoreQueryFilterType
)

data class FirestoreQueryParams(
    val filters: List<FirestoreQueryFilter> = emptyList(),
    val orderBy: String? = null,
    val orderDirection: Query.Direction = Query.Direction.ASCENDING,
    val limit: Long? = null,
    val startAfter: Any? = null,
    val endBefore: Any? = null
)