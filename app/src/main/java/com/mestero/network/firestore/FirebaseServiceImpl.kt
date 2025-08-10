//package com.mestero.network.firebase
//
//import com.google.firebase.firestore.DocumentReference
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.Query
//import com.google.firebase.firestore.QuerySnapshot
//import kotlinx.coroutines.tasks.await
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class FirestoreRepositoryImpl @Inject constructor(
//    private val firestore: FirebaseFirestore
//) : FirestoreRepository {
//
//    override suspend fun getDocument(collection: String, documentId: String): DocumentReference {
//        return firestore.collection(collection).document(documentId)
//    }
//
//    override suspend fun getDocuments(collection: String): QuerySnapshot {
//        return firestore.collection(collection).get().await()
//    }
//
//    override suspend fun addDocument(collection: String, data: Any): DocumentReference {
//        return firestore.collection(collection).add(data).await()
//    }
//
//    override suspend fun updateDocument(collection: String, documentId: String, data: Any) {
//        firestore.collection(collection).document(documentId).set(data).await()
//    }
//
//    override suspend fun deleteDocument(collection: String, documentId: String) {
//        firestore.collection(collection).document(documentId).delete().await()
//    }
//
//    override suspend fun queryDocuments(collection: String, field: String, value: Any): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrder(collection: String, field: String, value: Any, orderBy: String, limit: Long): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .orderBy(orderBy)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderDesc(collection: String, field: String, value: Any, orderBy: String, limit: Long): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .orderBy(orderBy, Query.Direction.DESCENDING)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderAndStartAfter(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .orderBy(orderBy)
//            .startAfter(startAfter)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderDescAndStartAfter(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .orderBy(orderBy, Query.Direction.DESCENDING)
//            .startAfter(startAfter)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderAndStartAfterAndEndBefore(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .orderBy(orderBy)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderDescAndStartAfterAndEndBefore(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .orderBy(orderBy, Query.Direction.DESCENDING)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderAndStartAfterAndEndBeforeAndWhereEqualTo(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .orderBy(orderBy)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderDescAndStartAfterAndEndBeforeAndWhereEqualTo(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .orderBy(orderBy, Query.Direction.DESCENDING)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderAndStartAfterAndEndBeforeAndWhereEqualToAndWhereGreaterThan(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any, whereGreaterThanField: String, whereGreaterThanValue: Any): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .whereGreaterThan(whereGreaterThanField, whereGreaterThanValue)
//            .orderBy(orderBy)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderDescAndStartAfterAndEndBeforeAndWhereEqualToAndWhereGreaterThan(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any, whereGreaterThanField: String, whereGreaterThanValue: Any): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .whereGreaterThan(whereGreaterThanField, whereGreaterThanValue)
//            .orderBy(orderBy, Query.Direction.DESCENDING)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderAndStartAfterAndEndBeforeAndWhereEqualToAndWhereGreaterThanAndWhereLessThan(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any, whereGreaterThanField: String, whereGreaterThanValue: Any, whereLessThanField: String, whereLessThanValue: Any): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .whereGreaterThan(whereGreaterThanField, whereGreaterThanValue)
//            .whereLessThan(whereLessThanField, whereLessThanValue)
//            .orderBy(orderBy)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderDescAndStartAfterAndEndBeforeAndWhereEqualToAndWhereGreaterThanAndWhereLessThan(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any, whereGreaterThanField: String, whereGreaterThanValue: Any, whereLessThanField: String, whereLessThanValue: Any): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .whereGreaterThan(whereGreaterThanField, whereGreaterThanValue)
//            .whereLessThan(whereLessThanField, whereLessThanValue)
//            .orderBy(orderBy, Query.Direction.DESCENDING)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderAndStartAfterAndEndBeforeAndWhereEqualToAndWhereGreaterThanAndWhereLessThanAndWhereArrayContains(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any, whereGreaterThanField: String, whereGreaterThanValue: Any, whereLessThanField: String, whereLessThanValue: Any, whereArrayContainsField: String, whereArrayContainsValue: Any): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .whereGreaterThan(whereGreaterThanField, whereGreaterThanValue)
//            .whereLessThan(whereLessThanField, whereLessThanValue)
//            .whereArrayContains(whereArrayContainsField, whereArrayContainsValue)
//            .orderBy(orderBy)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderDescAndStartAfterAndEndBeforeAndWhereEqualToAndWhereGreaterThanAndWhereLessThanAndWhereArrayContains(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any, whereGreaterThanField: String, whereGreaterThanValue: Any, whereLessThanField: String, whereLessThanValue: Any, whereArrayContainsField: String, whereArrayContainsValue: Any): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .whereGreaterThan(whereGreaterThanField, whereGreaterThanValue)
//            .whereLessThan(whereLessThanField, whereLessThanValue)
//            .whereArrayContains(whereArrayContainsField, whereArrayContainsValue)
//            .orderBy(orderBy, Query.Direction.DESCENDING)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderAndStartAfterAndEndBeforeAndWhereEqualToAndWhereGreaterThanAndWhereLessThanAndWhereArrayContainsAndWhereArrayContainsAny(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any, whereGreaterThanField: String, whereGreaterThanValue: Any, whereLessThanField: String, whereLessThanValue: Any, whereArrayContainsField: String, whereArrayContainsValue: Any, whereArrayContainsAnyField: String, whereArrayContainsAnyValue: List<Any>): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .whereGreaterThan(whereGreaterThanField, whereGreaterThanValue)
//            .whereLessThan(whereLessThanField, whereLessThanValue)
//            .whereArrayContains(whereArrayContainsField, whereArrayContainsValue)
//            .whereArrayContainsAny(whereArrayContainsAnyField, whereArrayContainsAnyValue)
//            .orderBy(orderBy)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderDescAndStartAfterAndEndBeforeAndWhereEqualToAndWhereGreaterThanAndWhereLessThanAndWhereArrayContainsAndWhereArrayContainsAny(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any, whereGreaterThanField: String, whereGreaterThanValue: Any, whereLessThanField: String, whereLessThanValue: Any, whereArrayContainsField: String, whereArrayContainsValue: Any, whereArrayContainsAnyField: String, whereArrayContainsAnyValue: List<Any>): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .whereGreaterThan(whereGreaterThanField, whereGreaterThanValue)
//            .whereLessThan(whereLessThanField, whereLessThanValue)
//            .whereArrayContains(whereArrayContainsField, whereArrayContainsValue)
//            .whereArrayContainsAny(whereArrayContainsAnyField, whereArrayContainsAnyValue)
//            .orderBy(orderBy, Query.Direction.DESCENDING)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderAndStartAfterAndEndBeforeAndWhereEqualToAndWhereGreaterThanAndWhereLessThanAndWhereArrayContainsAndWhereArrayContainsAnyAndWhereIn(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any, whereGreaterThanField: String, whereGreaterThanValue: Any, whereLessThanField: String, whereLessThanValue: Any, whereArrayContainsField: String, whereArrayContainsValue: Any, whereArrayContainsAnyField: String, whereArrayContainsAnyValue: List<Any>, whereInField: String, whereInValue: List<Any>): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .whereGreaterThan(whereGreaterThanField, whereGreaterThanValue)
//            .whereLessThan(whereLessThanField, whereLessThanValue)
//            .whereArrayContains(whereArrayContainsField, whereArrayContainsValue)
//            .whereArrayContainsAny(whereArrayContainsAnyField, whereArrayContainsAnyValue)
//            .whereIn(whereInField, whereInValue)
//            .orderBy(orderBy)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderDescAndStartAfterAndEndBeforeAndWhereEqualToAndWhereGreaterThanAndWhereLessThanAndWhereArrayContainsAndWhereArrayContainsAnyAndWhereIn(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any, whereGreaterThanField: String, whereGreaterThanValue: Any, whereLessThanField: String, whereLessThanValue: Any, whereArrayContainsField: String, whereArrayContainsValue: Any, whereArrayContainsAnyField: String, whereArrayContainsAnyValue: List<Any>, whereInField: String, whereInValue: List<Any>): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .whereGreaterThan(whereGreaterThanField, whereGreaterThanValue)
//            .whereLessThan(whereLessThanField, whereLessThanValue)
//            .whereArrayContains(whereArrayContainsField, whereArrayContainsValue)
//            .whereArrayContainsAny(whereArrayContainsAnyField, whereArrayContainsAnyValue)
//            .whereIn(whereInField, whereInValue)
//            .orderBy(orderBy, Query.Direction.DESCENDING)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderAndStartAfterAndEndBeforeAndWhereEqualToAndWhereGreaterThanAndWhereLessThanAndWhereArrayContainsAndWhereArrayContainsAnyAndWhereInAndWhereNotIn(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any, whereGreaterThanField: String, whereGreaterThanValue: Any, whereLessThanField: String, whereLessThanValue: Any, whereArrayContainsField: String, whereArrayContainsValue: Any, whereArrayContainsAnyField: String, whereArrayContainsAnyValue: List<Any>, whereInField: String, whereInValue: List<Any>, whereNotInField: String, whereNotInValue: List<Any>): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .whereGreaterThan(whereGreaterThanField, whereGreaterThanValue)
//            .whereLessThan(whereLessThanField, whereLessThanValue)
//            .whereArrayContains(whereArrayContainsField, whereArrayContainsValue)
//            .whereArrayContainsAny(whereArrayContainsAnyField, whereArrayContainsAnyValue)
//            .whereIn(whereInField, whereInValue)
//            .whereNotIn(whereNotInField, whereNotInValue)
//            .orderBy(orderBy)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderDescAndStartAfterAndEndBeforeAndWhereEqualToAndWhereGreaterThanAndWhereLessThanAndWhereArrayContainsAndWhereArrayContainsAnyAndWhereInAndWhereNotIn(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any, whereGreaterThanField: String, whereGreaterThanValue: Any, whereLessThanField: String, whereLessThanValue: Any, whereArrayContainsField: String, whereArrayContainsValue: Any, whereArrayContainsAnyField: String, whereArrayContainsAnyValue: List<Any>, whereInField: String, whereInValue: List<Any>, whereNotInField: String, whereNotInValue: List<Any>): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .whereGreaterThan(whereGreaterThanField, whereGreaterThanValue)
//            .whereLessThan(whereLessThanField, whereLessThanValue)
//            .whereArrayContains(whereArrayContainsField, whereArrayContainsValue)
//            .whereArrayContainsAny(whereArrayContainsAnyField, whereArrayContainsAnyValue)
//            .whereIn(whereInField, whereInValue)
//            .whereNotIn(whereNotInField, whereNotInValue)
//            .orderBy(orderBy, Query.Direction.DESCENDING)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderAndStartAfterAndEndBeforeAndWhereEqualToAndWhereGreaterThanAndWhereLessThanAndWhereArrayContainsAndWhereArrayContainsAnyAndWhereInAndWhereNotInAndWhereNotEqualTo(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any, whereGreaterThanField: String, whereGreaterThanValue: Any, whereLessThanField: String, whereLessThanValue: Any, whereArrayContainsField: String, whereArrayContainsValue: Any, whereArrayContainsAnyField: String, whereArrayContainsAnyValue: List<Any>, whereInField: String, whereInValue: List<Any>, whereNotInField: String, whereNotInValue: List<Any>, whereNotEqualToField: String, whereNotEqualToValue: Any): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .whereGreaterThan(whereGreaterThanField, whereGreaterThanValue)
//            .whereLessThan(whereLessThanField, whereLessThanValue)
//            .whereArrayContains(whereArrayContainsField, whereArrayContainsValue)
//            .whereArrayContainsAny(whereArrayContainsAnyField, whereArrayContainsAnyValue)
//            .whereIn(whereInField, whereInValue)
//            .whereNotIn(whereNotInField, whereNotInValue)
//            .whereNotEqualTo(whereNotEqualToField, whereNotEqualToValue)
//            .orderBy(orderBy)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderDescAndStartAfterAndEndBeforeAndWhereEqualToAndWhereGreaterThanAndWhereLessThanAndWhereArrayContainsAndWhereArrayContainsAnyAndWhereInAndWhereNotInAndWhereNotEqualTo(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any, whereGreaterThanField: String, whereGreaterThanValue: Any, whereLessThanField: String, whereLessThanValue: Any, whereArrayContainsField: String, whereArrayContainsValue: Any, whereArrayContainsAnyField: String, whereArrayContainsAnyValue: List<Any>, whereInField: String, whereInValue: List<Any>, whereNotInField: String, whereNotInValue: List<Any>, whereNotEqualToField: String, whereNotEqualToValue: Any): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .whereGreaterThan(whereGreaterThanField, whereGreaterThanValue)
//            .whereLessThan(whereLessThanField, whereLessThanValue)
//            .whereArrayContains(whereArrayContainsField, whereArrayContainsValue)
//            .whereArrayContainsAny(whereArrayContainsAnyField, whereArrayContainsAnyValue)
//            .whereIn(whereInField, whereInValue)
//            .whereNotIn(whereNotInField, whereNotInValue)
//            .whereNotEqualTo(whereNotEqualToField, whereNotEqualToValue)
//            .orderBy(orderBy, Query.Direction.DESCENDING)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderAndStartAfterAndEndBeforeAndWhereEqualToAndWhereGreaterThanAndWhereLessThanAndWhereArrayContainsAndWhereArrayContainsAnyAndWhereInAndWhereNotInAndWhereNotEqualToAndWhereGreaterThanOrEqualTo(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any, whereGreaterThanField: String, whereGreaterThanValue: Any, whereLessThanField: String, whereLessThanValue: Any, whereArrayContainsField: String, whereArrayContainsValue: Any, whereArrayContainsAnyField: String, whereArrayContainsAnyValue: List<Any>, whereInField: String, whereInValue: List<Any>, whereNotInField: String, whereNotInValue: List<Any>, whereNotEqualToField: String, whereNotEqualToValue: Any, whereGreaterThanOrEqualToField: String, whereGreaterThanOrEqualToValue: Any): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .whereGreaterThan(whereGreaterThanField, whereGreaterThanValue)
//            .whereLessThan(whereLessThanField, whereLessThanValue)
//            .whereArrayContains(whereArrayContainsField, whereArrayContainsValue)
//            .whereArrayContainsAny(whereArrayContainsAnyField, whereArrayContainsAnyValue)
//            .whereIn(whereInField, whereInValue)
//            .whereNotIn(whereNotInField, whereNotInValue)
//            .whereNotEqualTo(whereNotEqualToField, whereNotEqualToValue)
//            .whereGreaterThanOrEqualTo(whereGreaterThanOrEqualToField, whereGreaterThanOrEqualToValue)
//            .orderBy(orderBy)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderDescAndStartAfterAndEndBeforeAndWhereEqualToAndWhereGreaterThanAndWhereLessThanAndWhereArrayContainsAndWhereArrayContainsAnyAndWhereInAndWhereNotInAndWhereNotEqualToAndWhereGreaterThanOrEqualTo(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any, whereGreaterThanField: String, whereGreaterThanValue: Any, whereLessThanField: String, whereLessThanValue: Any, whereArrayContainsField: String, whereArrayContainsValue: Any, whereArrayContainsAnyField: String, whereArrayContainsAnyValue: List<Any>, whereInField: String, whereInValue: List<Any>, whereNotInField: String, whereNotInValue: List<Any>, whereNotEqualToField: String, whereNotEqualToValue: Any, whereGreaterThanOrEqualToField: String, whereGreaterThanOrEqualToValue: Any): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .whereGreaterThan(whereGreaterThanField, whereGreaterThanValue)
//            .whereLessThan(whereLessThanField, whereLessThanValue)
//            .whereArrayContains(whereArrayContainsField, whereArrayContainsValue)
//            .whereArrayContainsAny(whereArrayContainsAnyField, whereArrayContainsAnyValue)
//            .whereIn(whereInField, whereInValue)
//            .whereNotIn(whereNotInField, whereNotInValue)
//            .whereNotEqualTo(whereNotEqualToField, whereNotEqualToValue)
//            .whereGreaterThanOrEqualTo(whereGreaterThanOrEqualToField, whereGreaterThanOrEqualToValue)
//            .orderBy(orderBy, Query.Direction.DESCENDING)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderAndStartAfterAndEndBeforeAndWhereEqualToAndWhereGreaterThanAndWhereLessThanAndWhereArrayContainsAndWhereArrayContainsAnyAndWhereInAndWhereNotInAndWhereNotEqualToAndWhereGreaterThanOrEqualToAndWhereLessThanOrEqualTo(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any, whereGreaterThanField: String, whereGreaterThanValue: Any, whereLessThanField: String, whereLessThanValue: Any, whereArrayContainsField: String, whereArrayContainsValue: Any, whereArrayContainsAnyField: String, whereArrayContainsAnyValue: List<Any>, whereInField: String, whereInValue: List<Any>, whereNotInField: String, whereNotInValue: List<Any>, whereNotEqualToField: String, whereNotEqualToValue: Any, whereGreaterThanOrEqualToField: String, whereGreaterThanOrEqualToValue: Any, whereLessThanOrEqualToField: String, whereLessThanOrEqualToValue: Any): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .whereGreaterThan(whereGreaterThanField, whereGreaterThanValue)
//            .whereLessThan(whereLessThanField, whereLessThanValue)
//            .whereArrayContains(whereArrayContainsField, whereArrayContainsValue)
//            .whereArrayContainsAny(whereArrayContainsAnyField, whereArrayContainsAnyValue)
//            .whereIn(whereInField, whereInValue)
//            .whereNotIn(whereNotInField, whereNotInValue)
//            .whereNotEqualTo(whereNotEqualToField, whereNotEqualToValue)
//            .whereGreaterThanOrEqualTo(whereGreaterThanOrEqualToField, whereGreaterThanOrEqualToValue)
//            .whereLessThanOrEqualTo(whereLessThanOrEqualToField, whereLessThanOrEqualToValue)
//            .orderBy(orderBy)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//
//    override suspend fun queryDocumentsWithOrderDescAndStartAfterAndEndBeforeAndWhereEqualToAndWhereGreaterThanAndWhereLessThanAndWhereArrayContainsAndWhereArrayContainsAnyAndWhereInAndWhereNotInAndWhereNotEqualToAndWhereGreaterThanOrEqualToAndWhereLessThanOrEqualTo(collection: String, field: String, value: Any, orderBy: String, limit: Long, startAfter: Any, endBefore: Any, whereField: String, whereValue: Any, whereGreaterThanField: String, whereGreaterThanValue: Any, whereLessThanField: String, whereLessThanValue: Any, whereArrayContainsField: String, whereArrayContainsValue: Any, whereArrayContainsAnyField: String, whereArrayContainsAnyValue: List<Any>, whereInField: String, whereInValue: List<Any>, whereNotInField: String, whereNotInValue: List<Any>, whereNotEqualToField: String, whereNotEqualToValue: Any, whereGreaterThanOrEqualToField: String, whereGreaterThanOrEqualToValue: Any, whereLessThanOrEqualToField: String, whereLessThanOrEqualToValue: Any): QuerySnapshot {
//        return firestore.collection(collection)
//            .whereEqualTo(field, value)
//            .whereEqualTo(whereField, whereValue)
//            .whereGreaterThan(whereGreaterThanField, whereGreaterThanValue)
//            .whereLessThan(whereLessThanField, whereLessThanValue)
//            .whereArrayContains(whereArrayContainsField, whereArrayContainsValue)
//            .whereArrayContainsAny(whereArrayContainsAnyField, whereArrayContainsAnyValue)
//            .whereIn(whereInField, whereInValue)
//            .whereNotIn(whereNotInField, whereNotInValue)
//            .whereNotEqualTo(whereNotEqualToField, whereNotEqualToValue)
//            .whereGreaterThanOrEqualTo(whereGreaterThanOrEqualToField, whereGreaterThanOrEqualToValue)
//            .whereLessThanOrEqualTo(whereLessThanOrEqualToField, whereLessThanOrEqualToValue)
//            .orderBy(orderBy, Query.Direction.DESCENDING)
//            .startAfter(startAfter)
//            .endBefore(endBefore)
//            .limit(limit)
//            .get()
//            .await()
//    }
//}