package com.daemonz.animange.util

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.daemonz.animange.R

object ThemeManager {
    val themes = listOf(
        R.style.AppTheme,
        R.style.AppTheme1
    )

    fun changeTheme(
        context: Context,
        activity: FragmentActivity,
        sharePreferenceManager: SharePreferenceManager,
        themeIndex: Int
    ) {
        sharePreferenceManager.setInt(THEME_KEY, themeIndex)
        val intent = activity.intent
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        activity.finish()
        context.startActivity(intent)
    }

    fun getTheme(sharePreferenceManager: SharePreferenceManager): Int {
        val themeIndex = sharePreferenceManager.getInt(THEME_KEY, 0)
        return themes[themeIndex]
    }
}