package com.daemonz.animange.entity

import androidx.annotation.Keep

@Keep
data class MenuItem(
    val icon: Int,
    val title: String,
    val type: MenuItemType = MenuItemType.Arrow,
    val isShow: Boolean = true,
    val menuFunction: MenuItemFunction = MenuItemFunction.None,
    var isBold: Boolean = false
)

@Keep
enum class MenuItemType {
    Arrow,
    Theme
}

@Keep
enum class MenuItemFunction {
    None,
    AccountInfo,
    Favorites,
    UserManagement,
    FeedBack,
    Language,
    Notifications
}
