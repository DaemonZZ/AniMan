package com.daemonz.animange.entity

import androidx.annotation.Keep

@Keep
data class Comment(
    val id: String = "",
    val slug: String = "",
    val content: String = "",
    val user: User = User(),
    val createdAt: Long = 0,
    var bestReply: Comment? = null,
    var repliesCount: Int = 0,
    val replyFor: String? = null,
    var liked: List<String> = emptyList(),
)
