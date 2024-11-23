package com.daemonz.animange.datasource.firebase

import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.ACCOUNT_COLLECTION
import com.daemonz.animange.util.ACTIVITIES
import com.daemonz.animange.util.COMMENT_COLLECTION
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.RATING_COLLECTION
import com.daemonz.animange.util.SEARCH_HISTORY
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.AggregateField
import com.google.firebase.firestore.AggregateQuerySnapshot
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.time.Instant
import java.util.Date

class FireBaseDataBase(
    private val db: FirebaseFirestore
) {
    companion object {
        private const val TAG = "FireBaseDataBase"
    }
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

    fun getRatingBySlug(slug: String) = db.collection(RATING_COLLECTION)
        .where(Filter.equalTo("slug", slug))
        .get()

    fun getRatingAverage(slug: String) = db.collection(RATING_COLLECTION)
        .where(Filter.equalTo("slug", slug))
        .aggregate(AggregateField.average("rating")).get(AggregateSource.SERVER)

    fun getSearchHistory() =
        db.collection(SEARCH_HISTORY).document(LoginData.getActiveUser()?.id.toString()).get()

    fun getRatingBySlugs(slugs: List<String>) = db.collection(RATING_COLLECTION)
        .where(Filter.inArray("slug", slugs))
        .get()

    fun getTodayActiveCount(): Task<QuerySnapshot> {
        val pivot = Date().apply {
            time = Instant.now().toEpochMilli() - 24 * 60 * 60 * 1000
        }
        ALog.d(TAG, pivot.toString())
        return db.collection(ACTIVITIES).where(Filter.greaterThan("time", pivot)).get()
    }

    fun getActiveUsersFrom(date: Date?): Task<AggregateQuerySnapshot> =
        db.collection(ACCOUNT_COLLECTION).where(Filter.greaterThan("lastLogin", date)).count()
            .get(AggregateSource.SERVER)

    fun getListActiveUsersFrom(date: Date?): Task<QuerySnapshot> =
        db.collection(ACCOUNT_COLLECTION).where(Filter.greaterThan("lastLogin", date)).get()


}