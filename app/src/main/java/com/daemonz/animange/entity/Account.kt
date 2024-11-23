package com.daemonz.animange.entity

import androidx.annotation.Keep
import java.util.Date

@Keep
data class Account(
    var id: String? = null,
    var email: String? = null,
    var users: List<User> = emptyList(),
    var name: String? = null,
    var phone: String? = null,
    var region: String? = null,
    var lastLogin: Date? = null
)
