package com.daemonz.animange.entity

data class SettingItem(
    val icon: Int,
    val type: SettingItemType,
)

enum class SettingItemType {
    ACCOUNT_INFO,
    FAVOURITE,
    FEEDBACK,
    LOGOUT,
    LOGIN,
    USER,
}
