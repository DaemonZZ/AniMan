package com.daemonz.animange.entity

import androidx.annotation.Keep

@Keep
data class Comment(
    val id: String,
    val content: String,
    val user: User,
    val createdAt: Long,
    var bestReply: Comment? = null,
    var repliesCount: Int = 0,
    val replyFor: String? = null,
    var liked: List<String> = emptyList(),
)
