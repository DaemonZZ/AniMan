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
        ContextCompat.getColor(context, R.color.textGray_dark)

    override fun tabTextColorSelected(context: Context) =
        ContextCompat.getColor(context, R.color.icon_dark)

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
    override fun iconLock(): Int = R.drawable.ic_lock_night
    override fun chipBgSelected(): Int = R.drawable.round_corner_dp16
    override fun chipBg(): Int = R.drawable.round_corner_dp16_night
    override fun iconSearch(): Int = R.drawable.ic_search_dark
    override fun iconRate(): Int = R.drawable.rate_night
    override fun iconShare(): Int = R.drawable.share_night
    override fun dividerColorRes(): Int = R.color.icon_dark
    override fun iconFilter(): Int = R.drawable.ic_filter_night
    override fun bottomSheetBg(): Int = R.drawable.bottom_sheet_bg_night
    override fun dialogBg(): Int = R.drawable.dialog_bg_night
    override fun filledBtnDisableColor(context: Context): Int =
        ContextCompat.getColor(context, R.color.textGray_dark)

    override fun id(): Int {
        return 1
    }
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is LightTheme) return false
        return this.javaClass.simpleName == other.javaClass.simpleName
    }
}