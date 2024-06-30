package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.Comment
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.LoginData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor() : BaseViewModel() {

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> = _comments
    var waitingReplyFor: String? = null
    private val _onRepliesLoaded: MutableLiveData<Map<Int, List<Comment>>?> = MutableLiveData(null)
    val onRepliesLoaded: LiveData<Map<Int, List<Comment>>?> = _onRepliesLoaded


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