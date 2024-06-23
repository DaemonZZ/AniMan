package com.daemonz.animange.entity

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class User(
    var id: String? = null,
    var name: String? = null,
    var userType: UserType? = UserType.CHILD,
    var image: Int? = null,
    @field:JvmField
    val isMainUser: Boolean = false,
    @field:JvmField
    var isActive: Boolean = false,
    var favorites: List<FavouriteItem> = listOf(),
    var password: String? = null,
) : Serializable {
    fun isFavourite(slug: String) = favorites.any { it.slug == slug }
}

@Keep
enum class UserType {
    ADULT,
    CHILD,
    ADD,
}