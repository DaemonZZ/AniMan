package com.daemonz.animange.util

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.daemonz.animange.R

object ThemeManager {
    private val themes = mapOf(
        0 to R.style.AppTheme,
        1 to R.style.AppTheme1
    )
    var theme = R.style.AppTheme
    fun changeTheme(context: Context, activity: FragmentActivity, themeIndex: Int) {
        theme = themes[themeIndex] ?: R.style.AppTheme
        val intent = activity.intent
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        activity.finish()
        context.startActivity(intent)
    }
}