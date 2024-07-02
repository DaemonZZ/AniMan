package com.daemonz.animange.entity

data class MenuItem(
    val icon: Int,
    val title: String,
    val type: MenuItemType = MenuItemType.Arrow,
    val isShow: Boolean = true,
)

enum class MenuItemType {
    Arrow,
    Theme
}
