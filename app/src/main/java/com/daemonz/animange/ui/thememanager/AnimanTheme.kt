package com.daemonz.animange.ui.thememanager

import android.content.Context
import com.dolatkia.animatedThemeManager.AppTheme

interface AnimanTheme : AppTheme {
    fun mainTheme(): Int
    fun firstActivityBackgroundColor(context: Context): Int
    fun firstActivityTextColor(context: Context): Int
    fun firstActivityIconColor(context: Context): Int
    fun textNavColor(context: Context): Int
    fun iconTextColor(context: Context): Int
    fun homeNavIcon(context: Context): Int
    fun cinemaNavIcon(context: Context): Int
    fun seriesNavIcon(context: Context): Int
    fun tvShowNavIcon(context: Context): Int
    fun settingNavIcon(context: Context): Int
}