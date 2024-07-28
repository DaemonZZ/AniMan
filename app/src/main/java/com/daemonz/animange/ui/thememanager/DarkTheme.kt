package com.daemonz.animange.ui.thememanager

import android.content.Context
import androidx.core.content.ContextCompat
import com.daemonz.animange.R

class DarkTheme : AnimanTheme {
    override fun mainTheme() = R.style.AppTheme_Dark

    override fun firstActivityBackgroundColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.bg_dark)
    }

    override fun firstActivityTextColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.text_dark)
    }

    override fun firstActivityIconColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.icon_dark)
    }

    override fun textNavColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.textNav_dark)
    }

    override fun iconTextColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.iconText_dark)
    }

    override fun homeNavIcon() = R.drawable.home_selector_night

    override fun cinemaNavIcon() = R.drawable.movies_selector_night

    override fun seriesNavIcon() = R.drawable.series_selector_night

    override fun tvShowNavIcon() = R.drawable.tv_selector_night

    override fun settingNavIcon() = R.drawable.setting_selector_night

    override fun tabTextColorDefault(context: Context) =
        ContextCompat.getColor(context, R.color.textTab_dark)

    override fun tabTextColorSelected(context: Context) =
        ContextCompat.getColor(context, R.color.iconText_light)

    override fun menuItemBackground(context: Context) =
        ContextCompat.getColor(context, R.color.blueFrame_dark)

    override fun iconBack() = R.drawable.arrow_back_night
    override fun iconNext() = R.drawable.chevron_right_night
    override fun loadingIcon() = R.drawable.ic_loading_night
    override fun appLogo() = R.drawable.app_logo_night
    override fun appLogoLandscape() = R.drawable.logo_landscape_night
    override fun indicatorActive(context: Context) =
        ContextCompat.getColor(context, R.color.text_dark)

    override fun indicatorInactive(context: Context) =
        ContextCompat.getColor(context, R.color.grayFrame_dark)

    override fun textGray(context: Context) = ContextCompat.getColor(context, R.color.textGray_dark)
    override fun userMenuItem() = R.drawable.person_night

    override fun favoriteMenuItem() = R.drawable.bookmark_night

    override fun userManagementMenuItem() = R.drawable.personal_privacy_night

    override fun feedbackMenuItem() = R.drawable.help_night
    override fun menuBackground() = R.drawable.round_corner_night
    override fun bookmarkIcon(): Int = R.drawable.bookmark_night
    override fun bookmarkFilledIcon(): Int = R.drawable.bookmark_filled_night
    override fun iconClose(): Int = R.drawable.ic_close_dark
    override fun iconEdit(): Int = R.drawable.ico_edit_dark

    override fun id(): Int {
        return 1
    }
}