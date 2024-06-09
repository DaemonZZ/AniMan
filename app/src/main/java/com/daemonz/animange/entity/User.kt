package com.daemonz.animange.entity

data class User(
    val name: String? = null,
    val userType: UserType? = UserType.CHILD,
    val imageUrl: String? = null,
)

enum class UserType {
    ADJUST,
    CHILD,
}