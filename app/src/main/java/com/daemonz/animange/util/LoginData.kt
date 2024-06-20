package com.daemonz.animange.util

import com.daemonz.animange.entity.Account
import com.firebase.ui.auth.FirebaseUiException

object LoginData {
    var account: Account? = null
    var currentError: FirebaseUiException? = null
    fun getActiveUser() = account?.users?.firstOrNull { it.isActive }
}