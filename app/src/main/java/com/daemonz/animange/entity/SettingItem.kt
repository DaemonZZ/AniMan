package com.daemonz.animange.entity

data class SettingItem(
    val icon: Int,
    val type: SettingItemType,
)

enum class SettingItemType(val pos: Int) {
    ACCOUNT_INFO(1),
    FAVOURITE(2),
    FEEDBACK(3),
    LOGOUT(-1),
    LOGIN(0),
    USER(0),
}
