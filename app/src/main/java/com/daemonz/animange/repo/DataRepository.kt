package com.daemonz.animange.repo

import com.daemonz.animange.base.NetworkEntity
import com.daemonz.animange.datasource.firebase.FireBaseDataBase
import com.daemonz.animange.datasource.network.IWebService
import com.daemonz.animange.datasource.room.FavouriteDao
import com.daemonz.animange.entity.Account
import com.daemonz.animange.entity.Comment
import com.daemonz.animange.entity.FavouriteItem
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.ListData
import com.daemonz.animange.entity.User
import com.daemonz.animange.entity.UserType
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.ACCOUNT_COLLECTION
import com.daemonz.animange.util.COMMENT_COLLECTION
import com.daemonz.animange.util.Country
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.TypeList
import com.daemonz.animange.util.VERSION_COLLECTION
import com.daemonz.animange.util.VERSION_DOCS
import com.daemonz.animange.util.toFavouriteItem
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import retrofit2.Response
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

    suspend fun getHomeData(): ListData {
        return handleDataResponse(apiService.getHomeData())
    }

    suspend fun getNewFilms(): ListData {
        return handleDataResponse(apiService.getNewFilms())
    }

    suspend fun getSeriesInComing(): ListData {
        return handleDataResponse(apiService.getSeriesInComing())
    }

    suspend fun getListAnime(): ListData {
        return handleDataResponse(apiService.filterData(list = "hoat-hinh", country = "nhat-ban"))
    }

    suspend fun getListMovies(): ListData {
        return handleDataResponse(apiService.getMovies())
    }

    suspend fun getTvShows(): ListData {
        return handleDataResponse(apiService.getTvShows())
    }

    suspend fun getAllSeries(): ListData {
        return handleDataResponse(apiService.getAllSeries())
    }

    suspend fun getDataByCategory(category: String): ListData {
        return handleDataResponse(apiService.getCategoryData(category))
    }

    suspend fun loadPlayerData(slug: String): ListData {
        return handleDataResponse(apiService.getFilmData(slug))
    }

    suspend fun get24RelatedFilm(slug: String, category: String): ListData {
        val data = apiService.filterData(
            list = slug,
            category = category
        )
        return handleDataResponse(data)
    }

    suspend fun getListFilmVietNam(): ListData {
        val data = apiService.filterData(
            list = TypeList.Movie.value,
            country = Country.VietNam.value,
        )
        return handleDataResponse(data)
    }

    suspend fun searchFilm(query: String): ListData {
        return handleDataResponse(apiService.search(query))
    }

    // Local Handle

//     fun markItemAsFavourite(item: Item, img: String) {
//          dao.insertAll(item.toFavouriteItem(img))
//     }

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

    fun updateUser(name: String?, image: Int?, userId: String) {
        LoginData.account?.users?.firstOrNull { it.id == userId }?.apply {
            image?.let { this.image = it }
            name?.let { this.name = it }
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
}