package com.daemonz.animange.entity

import androidx.annotation.Keep

@Keep
data class User(
    val name: String? = null,
    val userType: UserType? = UserType.CHILD,
    val imageUrl: String? = null,
    @field:JvmField
    val isMainUser: Boolean = false,
    @field:JvmField
    var isActive: Boolean = false,
)

@Keep
enum class UserType {
    ADJUST,
    CHILD,
}