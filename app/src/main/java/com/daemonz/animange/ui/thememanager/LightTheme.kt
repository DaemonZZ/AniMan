package com.daemonz.animange.ui.thememanager

import android.content.Context
import androidx.core.content.ContextCompat
import com.daemonz.animange.R

class LightTheme : AnimanTheme {
    override fun mainTheme() = R.style.AppTheme

    override fun firstActivityBackgroundColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.bg_light)
    }

    override fun firstActivityTextColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.text_light)
    }

    override fun firstActivityIconColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.icon_light)
    }

    override fun textNavColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.textNav_light)
    }

    override fun iconTextColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.iconText_light)
    }

    override fun homeNavIcon(context: Context) = R.drawable.home_selector

    override fun cinemaNavIcon(context: Context) = R.drawable.movies_selector

    override fun seriesNavIcon(context: Context) = R.drawable.series_selector

    override fun tvShowNavIcon(context: Context) = R.drawable.tv_selector

    override fun settingNavIcon(context: Context) = R.drawable.setting_selector

    override fun tabTextColorDefault(context: Context) = R.color.textTab_light

    override fun tabTextColorSelected(context: Context) = R.color.iconText_dark

    override fun menuItemBackground(context: Context) = R.color.menuItemBg_light

    override fun id(): Int = 0
}