package com.daemonz.animange.entity

import androidx.annotation.Keep
import com.daemonz.animange.util.LoginData
import java.time.Instant
import java.util.Date

@Keep
data class Activity(
    val id: String? = null,
    val activity: UserAction? = null,
    val account: String? = LoginData.account?.id,
    val accountName: String? = LoginData.account?.name,
    val content: String? = null,
    val time: Date = Date.from(Instant.now()),
)

enum class UserAction {
    Login,
    Register,
    Logout,
    CreateEditUser,
    Follow,
    Rate,
    Comment,
    Unfollow,
    Watch,
    Share,
    Search
}
