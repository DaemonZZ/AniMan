package com.daemonz.animange.entity

import androidx.annotation.Keep

@Keep
data class MenuItem(
    val icon: Int,
    val title: String,
    val type: MenuItemType = MenuItemType.Arrow,
    val isShow: Boolean = true,
)

@Keep
enum class MenuItemType {
    Arrow,
    Theme
}
