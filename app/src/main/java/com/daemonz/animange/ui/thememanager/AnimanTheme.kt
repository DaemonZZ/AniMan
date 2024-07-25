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
    fun homeNavIcon(): Int
    fun cinemaNavIcon(): Int
    fun seriesNavIcon(): Int
    fun tvShowNavIcon(): Int
    fun settingNavIcon(): Int
    fun tabTextColorDefault(context: Context): Int
    fun tabTextColorSelected(context: Context): Int
    fun menuItemBackground(context: Context): Int
    fun iconBack(): Int
    fun iconNext(): Int
    fun loadingIcon(): Int
    fun appLogo(): Int
    fun appLogoLandscape(): Int
    fun indicatorActive(context: Context): Int
    fun indicatorInactive(context: Context): Int
    fun textGray(context: Context): Int
}