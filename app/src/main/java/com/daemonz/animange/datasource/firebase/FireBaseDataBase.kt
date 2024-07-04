package com.daemonz.animange.datasource.firebase

import com.daemonz.animange.util.COMMENT_COLLECTION
import com.daemonz.animange.util.RATING_COLLECTION
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

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
    fun getCommentsBySlug(slug: String) = db.collection(COMMENT_COLLECTION)
        .where(Filter.equalTo("slug", slug))
        .where(Filter.equalTo("replyFor", null)).orderBy("createdAt", Query.Direction.DESCENDING)
        .get()

    fun getReplyForComment(commentId: String) = db.collection(COMMENT_COLLECTION)
        .where(Filter.equalTo("replyFor", commentId))
        .orderBy("createdAt", Query.Direction.DESCENDING).get()
    fun getRating(slug: String, userId: String) = db.collection(RATING_COLLECTION)
        .where(Filter.equalTo("slug", slug))
        .where(Filter.equalTo("user.id", userId)).get()
}