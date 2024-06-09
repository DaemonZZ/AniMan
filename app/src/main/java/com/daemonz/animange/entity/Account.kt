package com.daemonz.animange.entity

data class Account(
    val email: String? = null,
    val users: List<User> = emptyList()
)
