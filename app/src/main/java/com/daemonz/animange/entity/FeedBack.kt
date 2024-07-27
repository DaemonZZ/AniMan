package com.daemonz.animange.entity

import androidx.annotation.Keep
import java.util.Date
import java.util.UUID

@Keep
data class FeedBack(
    val id: String = UUID.randomUUID().toString(),
    val content: String = "",
    val createdAt: Date = Date(),
    val email: String = "",
    val name: String = ""
)
