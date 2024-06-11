package com.daemonz.animange.entity

import androidx.annotation.Keep

@Keep
data class Account(
    val id: String,
    val email: String? = null,
    val users: List<User> = emptyList(),
    val name: String? = null,
)
