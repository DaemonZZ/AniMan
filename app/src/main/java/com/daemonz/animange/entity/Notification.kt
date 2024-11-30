package com.daemonz.animange.entity

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class Notification(
    val id: String? = null,
    val title: String? = null,
    val message: String? = null,
    val type: String? = null,
    val time: String? = null,
    val img: String? = null,
    val isRead: Boolean = false
) : Serializable
