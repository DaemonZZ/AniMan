package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.Comment
import com.daemonz.animange.entity.Episode
import com.daemonz.animange.entity.FilmRating
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.ListData
import com.daemonz.animange.entity.User
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.TypeList
import com.google.firebase.firestore.AggregateField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor() : BaseViewModel() {
    private val _playerData: MutableLiveData<ListData> = MutableLiveData()
    val playerData: LiveData<ListData> = _playerData

    private val _currentPlaying = MutableLiveData<Episode>()
    val currentPlaying: LiveData<Episode> = _currentPlaying

    private val _isFavourite = MutableLiveData<Boolean>()
    val isFavourite: LiveData<Boolean> = _isFavourite

    private val _lastRating = MutableLiveData<FilmRating?>()
    val lastRating: LiveData<FilmRating?> = _lastRating

    private val _allRatings = MutableLiveData<List<FilmRating>>()
    val allRatings: LiveData<List<FilmRating>> = _allRatings

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> = _comments
    var waitingReplyFor: String? = null
    private val _onRepliesLoaded: MutableLiveData<Map<Int, List<Comment>>?> = MutableLiveData(null)
    val onRepliesLoaded: LiveData<Map<Int, List<Comment>>?> = _onRepliesLoaded

    private val _rateAvg = MutableLiveData<Double>()
    val rateAvg: LiveData<Double> = _rateAvg

    fun loadData(item: String) {
        launchOnIO {
            val data = repository.loadPlayerData(item)
            val defaultEpisode = data.data.item?.episodes?.firstOrNull()
            withContext(Dispatchers.Main) {
                _playerData.value = data
                isItemFavourite()
                defaultEpisode?.let {
                    _currentPlaying.value = it.copy(
                        pivot = defaultEpisode.serverData.first().slug,
                    )
                }
            }
        }
    }

    fun chooseEpisode(episode: String, server: Int = 0) {
        if (episode == currentPlaying.value?.pivot && playerData.value?.data?.item?.episodes?.get(
                server
            )?.serverName == currentPlaying.value?.serverName
        ) {
            ALog.i(TAG, "Same Episode selected")
            return
        }
        val data = _playerData.value
        data?.data?.item?.episodes?.let {
            val eps = if (server == 0 || server >= data.data.item.episodes.size) {
                it.firstOrNull()
            } else {
                it[server]
            }
            _currentPlaying.value = eps?.copy(
                pivot = episode
            )
        } ?: kotlin.run {
            ALog.d(TAG, "chooseEpisode: data is null")
        }
    }

    fun markItemAsFavorite(item: Item? = null) = launchOnIO {
        ALog.d(TAG, "markItemAsFavourite: $item")
        playerData.value?.data?.let {
            if (item == null) {
                if (it.item != null) {
                    repository.markItemAsFavourite(
                        item = it.item,
                        img = it.getImageUrl().replace("thumb", "poster")
                    )
                    withContext(Dispatchers.Main) {
                        _isFavourite.value = true
                    }
                }
            } else {
                repository.markItemAsFavourite(
                    item = item,
                    item.getImageUrl(it.getImageUrl())
                )
            }
        }

    }

    fun unMarkItemAsFavorite(item: Item? = null) = launchOnIO {
        ALog.d(TAG, "unMarkItemAsFavourite: $item")
        if (item == null) {
            playerData.value?.data?.let {
                if (it.item != null) {
                    repository.unMarkItemAsFavourite(it.item)
                    withContext(Dispatchers.Main) {
                        _isFavourite.value = false
                    }
                }
            }
        } else {
            repository.unMarkItemAsFavourite(item)
        }

    }

    private suspend fun isItemFavourite() = launchOnIO {
        val res =
            playerData.value?.data?.item?.slug?.let {
                LoginData.getActiveUser()?.isFavourite(it)
            } ?: false
        withContext(Dispatchers.Main) {
            _isFavourite.value = res
        }
    }

    fun rateItem(score: Int, comment: String, currentId: String?) = launchOnIO {
        val user = LoginData.getActiveUser()?.let {
            User(
                id = it.id,
                name = it.name,
                image = it.image,
            )
        } ?: User()
        val rating = FilmRating(
            id = currentId ?: UUID.randomUUID().toString(),
            slug = playerData.value?.data?.item?.slug.toString(),
            rating = (score + 1).toDouble(),
            comment = comment,
            user = user,
        )
        repository.rateItem(rating).addOnSuccessListener {
            getAllRating(playerData.value?.data?.item?.slug.toString())
            getRatingAvg(playerData.value?.data?.item?.slug.toString())
        }
    }

    fun getRating(slug: String, userId: String) = launchOnIO {
        repository.getRating(slug, userId).addOnSuccessListener {
            ALog.d(TAG, "getRating: ${it.toObjects(FilmRating::class.java)}}")
            val data = it.toObjects(FilmRating::class.java)
            launchOnUI {
                if (data.isNotEmpty()) {
                    _lastRating.value = data.first()
                } else {
                    _lastRating.value = null
                }
            }
        }.addOnFailureListener {
            launchOnIO {
                errorMessage.value = it.message
            }
        }
    }

    fun getAllRating(slug: String) = launchOnIO {
        repository.getRatingBySlug(slug)
            .addOnSuccessListener {
                ALog.d(TAG, "getAllRating: ${it.toObjects(FilmRating::class.java)}")
                val data = it.toObjects(FilmRating::class.java)
                launchOnUI {
                    _allRatings.value = data
                }
            }.addOnFailureListener {
                launchOnUI {
                    errorMessage.value = it.message
                }
            }
    }

    fun getRatingAvg(slug: String) = launchOnIO {
        repository.getRatingAvg(slug).addOnCompleteListener {
            if (it.isSuccessful) {
                val data = it.result.getDouble(AggregateField.average("rating"))
                ALog.d(TAG, "getRatingAvg: $data")
                launchOnUI {
                    _rateAvg.value = data ?: 0.0
                }
            } else {
//                ALog.e(TAG, "getRatingAvg: ${it.exception?.message}")
            }
        }
    }

    //Comment
    fun sendComment(comment: Comment) = launchOnIO {
        repository.sendComment(comment)?.addOnSuccessListener {
            //update parrent comment
            comment.replyFor?.let {
                repository.getCommentById(it).addOnSuccessListener {
                    ALog.d(TAG, "getParentComment: success ${it}")
                    it.toObject(Comment::class.java)?.let { parent ->
                        repository.onReplyForComment(
                            parentComment = parent,
                            child = comment
                        ).addOnSuccessListener {
                            ALog.d(TAG, "onReplyForComment: success")
                            loadComments(parent.slug)
                        }
                    }
                }

            } ?: kotlin.run {
                loadComments(comment.slug)
            }
        }
    }

    fun loadComments(slug: String) = launchOnIO {
        repository.getCommentsBySlug(slug).addOnSuccessListener {
            it.toObjects(Comment::class.java).let {
                ALog.d(TAG, "loadComments: ${it}")
                launchOnUI {
                    _comments.value = it
                }
            }
        }.addOnFailureListener {
            ALog.e(TAG, "loadComments: $it")
            launchOnUI {
                errorMessage.value = it.message
            }
        }
    }

    fun loadReplies(parent: Comment, pos: Int) = launchOnIO {
        ALog.d(TAG, "loadReplies: $parent")
        repository.loadRepliesForComment(parent.id).addOnSuccessListener {
            ALog.d(TAG, "loadReplies: success $it")
            it.toObjects(Comment::class.java).let { replyList ->
                if (replyList.isNotEmpty()) {
                    launchOnUI {
                        _onRepliesLoaded.value = mapOf(pos to replyList)
                    }
                }
            }
        }.addOnFailureListener {
            ALog.e(TAG, "loadReplies: $it")
            launchOnUI {
                errorMessage.value = it.message
            }
        }
    }

    fun toggleLike(comment: Comment) {
        ALog.d(TAG, "toggleLike: $comment")
        LoginData.getActiveUser()?.id?.let { user ->
            if (comment.liked.contains(user)) {
                comment.liked = comment.liked.toMutableList().apply { remove(user) }
            } else {
                comment.liked = comment.liked.toMutableList().apply { add(user) }
            }
            repository.toggleLikeComment(comment).addOnSuccessListener {
                loadComments(comment.slug)
            }
        }
    }

}