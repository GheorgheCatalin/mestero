package com.mestero.network.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Transaction
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirestoreRepository {

    override suspend fun getDocument(collection: String, documentId: String): DocumentReference {
        return firestore.collection(collection).document(documentId)
    }

    override suspend fun getDocuments(collection: String): QuerySnapshot {
        return firestore.collection(collection).get().await()
    }

    override suspend fun addDocument(collection: String, data: Any): DocumentReference {
        return firestore.collection(collection).add(data).await()
    }

    override suspend fun updateDocument(collection: String, documentId: String, data: Any) {
        firestore.collection(collection)
            .document(documentId)
            .update(data as Map<String, Any>)
            .await()
    }

    override suspend fun deleteDocument(collection: String, documentId: String) {
        firestore.collection(collection)
            .document(documentId)
            .delete()
            .await()
    }

    override suspend fun queryDocuments(collection: String, params: FirestoreQueryParams): QuerySnapshot {
        var query: Query = firestore.collection(collection)

        for (filter in params.filters) {
            // Predefined filters covering most common use cases Firestore query use cases
            query = when (filter.type) {
                FirestoreQueryFilterType.EQUAL_TO -> query.whereEqualTo(filter.field, filter.value)
                FirestoreQueryFilterType.NOT_EQUAL_TO -> query.whereNotEqualTo(filter.field, filter.value)
                FirestoreQueryFilterType.GREATER_THAN -> query.whereGreaterThan(filter.field, filter.value)
                FirestoreQueryFilterType.GREATER_THAN_OR_EQUAL_TO -> query.whereGreaterThanOrEqualTo(filter.field, filter.value)
                FirestoreQueryFilterType.LESS_THAN -> query.whereLessThan(filter.field, filter.value)
                FirestoreQueryFilterType.LESS_THAN_OR_EQUAL_TO -> query.whereLessThanOrEqualTo(filter.field, filter.value)
                FirestoreQueryFilterType.ARRAY_CONTAINS -> query.whereArrayContains(filter.field, filter.value)
                FirestoreQueryFilterType.ARRAY_CONTAINS_ANY -> query.whereArrayContainsAny(filter.field, filter.value as List<*>)
                FirestoreQueryFilterType.IN -> query.whereIn(filter.field, filter.value as List<*>)
                FirestoreQueryFilterType.NOT_IN -> query.whereNotIn(filter.field, filter.value as List<*>)
            }
        }

        params.orderBy?.let {
            query = query.orderBy(it, params.orderDirection)
        }
        // TODO check if needed   for loading large nb of listings with pagination
        params.startAfter?.let {
            query = query.startAfter(it)
        }
        // TODO check if needed  - same as above
        params.endBefore?.let {
            query = query.endBefore(it)
        }

        params.limit?.let {
            query = query.limit(it)
        }

        return query.get().await()
    }

    override suspend fun getDocumentData(collection: String, documentId: String): DocumentSnapshot? {
        return try {
            firestore.collection(collection).document(documentId).get().await()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun queryByField(collection: String, field: String, value: Any): QuerySnapshot {
        return firestore.collection(collection)
            .whereEqualTo(field, value)
            .get()
            .await()
    }

    override suspend fun queryByFieldWithLimit(collection: String, field: String, value: Any, limit: Long): QuerySnapshot {
        return firestore.collection(collection)
            .whereEqualTo(field, value)
            .limit(limit)
            .get()
            .await()
    }

    override suspend fun queryByFieldWithOrder(collection: String, field: String, value: Any, orderBy: String, orderDirection: Query.Direction): QuerySnapshot {
        return firestore.collection(collection)
            .whereEqualTo(field, value)
            .orderBy(orderBy, orderDirection)
            .get()
            .await()
    }

    // Transaction support
    override suspend fun runTransaction(updateFunction: (Transaction) -> Unit) {
        firestore.runTransaction { transaction ->
            updateFunction(transaction)
            null // Won't need value returned from transaction
        }.await()
    }

    // Collection reference access
    override fun getCollectionReference(collection: String): CollectionReference {
        return firestore.collection(collection)
    }
} 