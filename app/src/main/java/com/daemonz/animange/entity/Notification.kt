package com.daemonz.animange.entity

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class Notification(
    val id: String? = null,
    val title: String? = null,
    val message: String? = null,
    val type: NotificationType? = null,
    val time: String? = null,
    val img: String? = null,
    val isRead: Boolean = false,
    val isNew: Boolean = false,
    val destination: String? = null
) : Serializable

enum class NotificationType {
    NEW_UPDATE,
    REPLY_FEEDBACK,
    NEWS
}
