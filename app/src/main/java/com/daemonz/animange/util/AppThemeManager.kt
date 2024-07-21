package com.daemonz.animange.util

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentActivity
import com.daemonz.animange.R

object AppThemeManager {
    val themes = listOf(
        R.style.AppTheme,
        R.style.AppTheme1,
        R.style.AppTheme2,
        R.style.AppTheme3
    )

    fun changeTheme(
        activity: FragmentActivity,
        theme: Int
    ) {
        activity.recreate()
    }

    fun getTheme(sharePreferenceManager: SharePreferenceManager): Int {
        val themeIndex = sharePreferenceManager.getInt(THEME_KEY, 0)
        return themes[themeIndex]
    }

    fun setNightMode(nightMode: Boolean) {
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}