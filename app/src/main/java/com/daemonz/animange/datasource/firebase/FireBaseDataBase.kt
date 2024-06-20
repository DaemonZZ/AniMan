package com.daemonz.animange.datasource.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

class FireBaseDataBase(
    private val db: FirebaseFirestore
) {
    fun getCollection(collectionName: String) = db.collection(collectionName)
    fun getDocument(collectionName: String, documentId: String) =
        db.collection(collectionName).document(documentId)

    fun getQuery(collectionName: String) = db.collection(collectionName)
    fun getQuery(collectionName: String, documentId: String) =
        db.collection(collectionName).document(documentId)

    fun getQuery(collectionName: String, documentId: String, field: String) =
        db.collection(collectionName).document(documentId).collection(field)

    fun getQuery(collectionName: String, documentId: String, field: String, subField: String) =
        db.collection(collectionName).document(documentId).collection(field).document(subField)

    fun getQuery(
        collectionName: String,
        documentId: String,
        field: String,
        subField: String,
        subSubField: String
    ) = db.collection(collectionName).document(documentId).collection(field).document(subField)
        .collection(subSubField)

    fun addDocument(
        collectionName: String,
        documentId: String,
        data: Any
    ): Task<Void> {
        return db.collection(collectionName).document(documentId).set(data)
    }

    fun updateDocument(
        collectionName: String,
        documentId: String,
        data: HashMap<String, Any>
    ): Task<Void> {
        return db.collection(collectionName).document(documentId).update(data)
    }

    fun deleteDocument(collectionName: String, documentId: String): Task<Void> {
        return db.collection(collectionName).document(documentId).delete()
    }
}