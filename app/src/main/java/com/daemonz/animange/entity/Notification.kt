package com.daemonz.animange.entity

import androidx.annotation.Keep
import java.io.Serializable
import java.time.Instant
import java.util.Date
import java.util.UUID

@Keep
data class Notification(
    var id: String? = null,
    var title: String? = null,
    var vietnameseTitle: String? = null,
    var message: String? = null,
    var vietnameseMessage: String? = null,
    var account: String? = null,
    var accountName: String? = null,
    var isGlobal: Boolean = true,
    var isVietnamese: Boolean = false,
    var type: NotificationType? = null,
    var time: Date? = null,
    var img: String? = null,
    @field:JvmField var isRead: Boolean = false,
    @field:JvmField var isNew: Boolean = false,
    var destination: String = GLOBAL
) : Serializable {
    companion object {
        const val GLOBAL = "global"
        fun dummy(): Notification = Notification(
            id = UUID.randomUUID().toString(),
            title = "New Update",
            message = "AniMan has released a new update!",
            type = NotificationType.NEW_UPDATE,
            time = Date.from(Instant.now()),
            img = null,
            isRead = false,
            isNew = true,
            destination = "sgooophx@gmail.com"
        )
    }

    fun getTitle(region: String): String? = when (region) {
        "vn" -> vietnameseTitle ?: title
        else -> title
    }

    fun getMessage(region: String): String? = when (region) {
        "vn" -> vietnameseMessage ?: message
        else -> message
    }
}

enum class NotificationType {
    NEW_UPDATE,
    REPLY_FEEDBACK,
    NEWS
}

