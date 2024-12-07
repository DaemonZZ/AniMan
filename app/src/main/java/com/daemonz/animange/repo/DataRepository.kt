package com.daemonz.animange.repo

import com.daemonz.animange.base.NetworkEntity
import com.daemonz.animange.datasource.firebase.FireBaseDataBase
import com.daemonz.animange.datasource.network.IWebService
import com.daemonz.animange.datasource.room.FavouriteDao
import com.daemonz.animange.entity.Account
import com.daemonz.animange.entity.Activity
import com.daemonz.animange.entity.Comment
import com.daemonz.animange.entity.FavouriteItem
import com.daemonz.animange.entity.FeedBack
import com.daemonz.animange.entity.FilmRating
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.ListData
import com.daemonz.animange.entity.Notification
import com.daemonz.animange.entity.SearchHistory
import com.daemonz.animange.entity.SearchHistoryData
import com.daemonz.animange.entity.User
import com.daemonz.animange.entity.UserAction
import com.daemonz.animange.entity.UserType
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.ACCOUNT_COLLECTION
import com.daemonz.animange.util.ACTIVITIES
import com.daemonz.animange.util.COMMENT_COLLECTION
import com.daemonz.animange.util.Country
import com.daemonz.animange.util.FEEDBACK_COLLECTION
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.RATING_COLLECTION
import com.daemonz.animange.util.SEARCH_HISTORY
import com.daemonz.animange.util.TypeList
import com.daemonz.animange.util.VERSION_COLLECTION
import com.daemonz.animange.util.VERSION_DOCS
import com.daemonz.animange.util.toFavouriteItem
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.AggregateQuerySnapshot
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.QuerySnapshot
import retrofit2.Response
import java.time.Instant
import java.util.Calendar
import java.util.Date
import java.util.UUID

class DataRepository(
    private val apiService: IWebService,
    private val dao: FavouriteDao,
    private val fireStoreDataBase: FireBaseDataBase
) {
    companion object {
        private const val TAG = "DataRepository"
    }
    // API Handle

    private fun <T : NetworkEntity> handleDataResponse(response: Response<T>): T {
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else if (!response.isSuccessful && response.errorBody() != null) {
            (NetworkEntity() as T).apply { error = response.errorBody()?.string() }
        } else {
            NetworkEntity() as T
        }
    }

    suspend fun getHomeData(): Response<ListData> {
        return apiService.getHomeData()
    }

    suspend fun getNewFilms(): ListData {
        return handleDataResponse(apiService.getNewFilms())
    }

    suspend fun getSeriesInComing(): Response<ListData> {
        return apiService.getSeriesInComing()
    }

    suspend fun getListAnime(): Response<ListData> {
        return apiService.filterData(list = "hoat-hinh", country = "nhat-ban")
    }

    suspend fun getListMovies(page: String = ""): Response<ListData> {
        return apiService.getMovies(page)
    }

    suspend fun getTvShows(page: String) = apiService.getTvShows(page)

    suspend fun getAllSeries(page: String) = apiService.getAllSeries(page)

    suspend fun getDataByCategory(category: String, page: String) =
        apiService.getCategoryData(category, page)

    suspend fun loadPlayerData(slug: String): ListData {
        return handleDataResponse(apiService.getFilmData(slug))
    }

    suspend fun getRelatedFilms(slug: String, category: String, page: String) =
        apiService.filterData(
            list = slug,
            category = category,
            page = page
        )


    suspend fun getListFilmVietNam(): Response<ListData> {
        val data = apiService.filterData(
            list = TypeList.Movie.value,
            country = Country.VietNam.value,
        )
        return data
    }

    suspend fun searchFilm(query: String, page: String) = apiService.search(query, page)

    fun unMarkItemAsFavourite(slug: String) {
        dao.delete(slug)
    }

    fun isItemFavourite(slug: String): Boolean {
        return dao.loadAllBySlug(slug).isNotEmpty()
    }

    fun getAllFavourite(): List<FavouriteItem> {
        return dao.getAll()
    }

    //FireBase Handle

    fun getAccount(id: String): Task<DocumentSnapshot> {
        return fireStoreDataBase.getDocument(collectionName = ACCOUNT_COLLECTION, documentId = id)
            .get()
    }

    fun getUpdateData() = fireStoreDataBase.getDocument(
        collectionName = VERSION_COLLECTION,
        documentId = VERSION_DOCS
    ).get()

    fun saveAccount(account: Account) {
        fireStoreDataBase.addDocument(
            collectionName = ACCOUNT_COLLECTION,
            documentId = account.id.toString(),
            data = account
        ).addOnSuccessListener {
            ALog.d(TAG, "saveAccount: success")
            LoginData.account = account
        }
    }

    fun markItemAsFavourite(item: Item, img: String) {
        LoginData.getActiveUser()?.let {
            if (!it.isFavourite(item.slug)) {
                val list = it.favorites.toMutableList().apply {
                    add(item.toFavouriteItem(img))
                }
                LoginData.getActiveUser()?.favorites = list
                fireStoreDataBase.addDocument(
                    collectionName = ACCOUNT_COLLECTION,
                    documentId = LoginData.account?.id.toString(),
                    data = LoginData.account!!
                )
                ALog.d(TAG, "markItemAsFavourite: ${LoginData.getActiveUser()?.favorites?.size}")
            }
        }
        val activity = Activity(
            id = UUID.randomUUID().toString(),
            activity = UserAction.Follow,
            content = "${LoginData.account?.name} matched ${item.name} as favorite"
        )
//        syncActivity(activity)
    }

    fun unMarkItemAsFavourite(item: Item) {
        LoginData.getActiveUser()?.let {
            if (it.isFavourite(item.slug)) {
                val list = it.favorites.toMutableList().apply {
                    removeIf { it.slug == item.slug }
                }
                LoginData.getActiveUser()?.favorites = list
                fireStoreDataBase.addDocument(
                    collectionName = ACCOUNT_COLLECTION,
                    documentId = LoginData.account?.id.toString(),
                    data = LoginData.account!!
                )
            }
        }
        val activity = Activity(
            id = UUID.randomUUID().toString(),
            activity = UserAction.Unfollow,
            content = "${LoginData.account?.name} un-matched ${item.name} as favorite"
        )
        syncActivity(activity)
    }

    fun markItemAsFavourite(item: FavouriteItem) {
        LoginData.getActiveUser()?.let {
            if (!it.isFavourite(item.slug)) {
                val list = it.favorites.toMutableList().apply {
                    add(item)
                }
                LoginData.getActiveUser()?.favorites = list
                fireStoreDataBase.addDocument(
                    collectionName = ACCOUNT_COLLECTION,
                    documentId = LoginData.account?.id.toString(),
                    data = LoginData.account!!
                )
                ALog.d(TAG, "markItemAsFavourite: ${LoginData.getActiveUser()?.favorites?.size}")
            }
        }
        val activity = Activity(
            id = UUID.randomUUID().toString(),
            activity = UserAction.Follow,
            content = "${LoginData.account?.name} matched ${item.name} as favorite"
        )
        syncActivity(activity)
    }

    fun unMarkItemAsFavourite(item: FavouriteItem) {
        LoginData.getActiveUser()?.let {
            if (it.isFavourite(item.slug)) {
                val list = it.favorites.toMutableList().apply {
                    removeIf { it.slug == item.slug }
                }
                LoginData.getActiveUser()?.favorites = list
                fireStoreDataBase.addDocument(
                    collectionName = ACCOUNT_COLLECTION,
                    documentId = LoginData.account?.id.toString(),
                    data = LoginData.account!!
                )
            }
        }
        val activity = Activity(
            id = UUID.randomUUID().toString(),
            activity = UserAction.Unfollow,
            content = "${LoginData.account?.name} un-matched ${item.name} as favorite"
        )
        syncActivity(activity)
    }

    fun updateProfile(name: String, email: String, phone: String) {
        LoginData.account?.apply {
            this.name = name
            this.email = email
            this.phone = phone
            fireStoreDataBase.addDocument(
                collectionName = ACCOUNT_COLLECTION,
                documentId = this.id.toString(),
                data = this
            ).addOnSuccessListener {
                ALog.d(TAG, "updateProfile: success")
            }
        }

    }

    fun updateUser(name: String?, password: String?, image: Int?, userId: String) {
        LoginData.account?.users?.firstOrNull { it.id == userId }?.apply {
            image?.let { this.image = it }
            name?.let { this.name = it }
            password?.let { this.password = it }
            fireStoreDataBase.addDocument(
                collectionName = ACCOUNT_COLLECTION,
                documentId = LoginData.account?.id.toString(),
                data = LoginData.account!!
            ).addOnSuccessListener {
                ALog.d(TAG, "updateAvatar: success")
            }
        }
    }

    fun newUser(name: String?, image: Int, userType: UserType, password: String?) {
        val newUser = User(
            id = UUID.randomUUID().toString(),
            name = name,
            image = image,
            userType = userType,
            password = password,
            isMainUser = false,
            createdAt = Date.from(Instant.now())
        )
        val listUser = LoginData.account?.users?.toMutableList()?.apply {
            add(newUser)
        }
        listUser?.let {
            LoginData.account?.users = it
            fireStoreDataBase.addDocument(
                collectionName = ACCOUNT_COLLECTION,
                documentId = LoginData.account?.id.toString(),
                data = LoginData.account!!
            ).addOnSuccessListener {
                ALog.d(TAG, "newUser: success")
            }
        }
    }

    fun switchUser(id: String) {
        LoginData.getActiveUser()?.isActive = false
        LoginData.account?.users?.firstOrNull { it.id == id }?.isActive = true
        LoginData.account?.let {
            fireStoreDataBase.addDocument(
                collectionName = ACCOUNT_COLLECTION,
                documentId = it.id.toString(),
                data = it
            ).addOnSuccessListener {
                ALog.d(TAG, "switchUser: success")
            }
        }
    }

    fun sendComment(comment: Comment): Task<Void>? {
        return LoginData.getActiveUser()?.let {
            fireStoreDataBase.addDocument(
                collectionName = COMMENT_COLLECTION,
                documentId = comment.id,
                data = comment
            )
        }
    }

    fun getCommentsBySlug(slug: String) = fireStoreDataBase.getCommentsBySlug(slug)
    fun getCommentById(id: String) = fireStoreDataBase.getDocument(COMMENT_COLLECTION, id).get()
    fun onReplyForComment(child: Comment, parentComment: Comment) =
        fireStoreDataBase.updateDocument(
            collectionName = COMMENT_COLLECTION,
            documentId = parentComment.id,
            data = hashMapOf(
                "bestReply" to child,
                "repliesCount" to parentComment.repliesCount + 1,
            )
        )

    fun loadRepliesForComment(id: String) =
        fireStoreDataBase.getReplyForComment(id)

    fun toggleLikeComment(comment: Comment) =
        fireStoreDataBase.updateDocument(
            collectionName = COMMENT_COLLECTION,
            documentId = comment.id,
            data = hashMapOf(
                "liked" to comment.liked,
            )
        )

    fun updateAccount(account: Account) =
        fireStoreDataBase.addDocument(
            collectionName = ACCOUNT_COLLECTION,
            documentId = account.id.toString(),
            data = account
        )

    fun rateItem(rating: FilmRating) =
        fireStoreDataBase.addDocument(
            collectionName = RATING_COLLECTION,
            documentId = rating.id,
            data = rating
        )

    fun getRatingBySlug(slug: String): Task<QuerySnapshot> {
        ALog.d(TAG, "getRatingBySlug: $slug")
        return fireStoreDataBase.getRatingBySlug(slug)
    }
    fun getRatingBySlugs(slugs: List<String>): Task<QuerySnapshot> {
        ALog.d(TAG, "getRatingBySlugs: $slugs")
        return fireStoreDataBase.getRatingBySlugs(slugs)
    }

    fun getRating(slug: String, userId: String) = fireStoreDataBase.getRating(slug, userId)
    fun getRatingAvg(slug: String) = fireStoreDataBase.getRatingAverage(slug)

    fun sendFeedBack(data: FeedBack): Task<Void> {
        return fireStoreDataBase.addDocument(
            collectionName = FEEDBACK_COLLECTION,
            documentId = data.id,
            data = data
        )
    }

    fun getSearchHistory() = fireStoreDataBase.getSearchHistory()

    fun updateSearchHistory(items: List<SearchHistory>) = fireStoreDataBase.addDocument(
        collectionName = SEARCH_HISTORY,
        documentId = LoginData.getActiveUser()?.id.toString(),
        data = SearchHistoryData(items)
    )

    fun getTotalUsersCount() =
        fireStoreDataBase.getCollection(ACCOUNT_COLLECTION).count().get(AggregateSource.SERVER)
    fun countNewUsersFromDate(date: Date) =
        fireStoreDataBase.countNewUsersFromDate(date)
    fun countNewUsersFromDateToDate(from: Date, to: Date) =
        fireStoreDataBase.countNewUsersFromDateToDate(from, to)

    fun getActiveUsersIn(h: Int): Task<AggregateQuerySnapshot> {
        val milis = h * 60 * 60 * 1000
        val date = Date.from(Instant.ofEpochMilli(Instant.now().toEpochMilli() - milis))
        return fireStoreDataBase.getActiveUsersFrom(date)
    }

    fun getListActiveUsersIn(h: Int): Task<QuerySnapshot> {
        val milis = h * 60 * 60 * 1000
        val date = Date.from(Instant.ofEpochMilli(Instant.now().toEpochMilli() - milis))
        return fireStoreDataBase.getListActiveUsersFrom(date)
    }


    fun getListAccounts(): Task<QuerySnapshot> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -7)
        return fireStoreDataBase.getCollection(ACCOUNT_COLLECTION)
            .where(Filter.greaterThan("createdAt", cal.time)).get()
    }
    fun syncActivity(activity: Activity) = fireStoreDataBase.addDocument(
        collectionName = ACTIVITIES,
        documentId = activity.id.toString(),
        data = activity
    )
    fun getNotifications() = fireStoreDataBase.getNotifications()
    fun createNotifications(notification: Notification) =
        fireStoreDataBase.newNotification(notification)

    fun markNotiAsRead(notification: Notification) =
        fireStoreDataBase.markNotificationAsRead(notification)

    fun markNotiAsOld(notification: Notification) =
        fireStoreDataBase.markNotificationAsOld(notification)
    fun getTotalUsersActiveToday() =
        fireStoreDataBase.getTodayActiveCount()
}