package com.daemonz.animange.entity

import androidx.annotation.Keep

@Keep
data class SettingItem(
    val icon: Int,
    val type: SettingItemType,
    var isShow: Boolean = true,
)

@Keep
enum class SettingItemType(val pos: Int) {
    LOGIN(0),
    USER(1),
    TITLE(2),
    ACCOUNT_INFO(3),
    FAVOURITE(4),
    FEEDBACK(5),
    LOGOUT(6),
}
