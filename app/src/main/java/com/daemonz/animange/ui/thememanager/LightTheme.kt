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

    override fun homeNavIcon() = R.drawable.home_selector

    override fun cinemaNavIcon() = R.drawable.movies_selector

    override fun seriesNavIcon() = R.drawable.series_selector

    override fun tvShowNavIcon() = R.drawable.tv_selector

    override fun settingNavIcon() = R.drawable.setting_selector

    override fun tabTextColorDefault(context: Context) =
        ContextCompat.getColor(context, R.color.textGray_light)

    override fun tabTextColorSelected(context: Context) =
        ContextCompat.getColor(context, R.color.icon_light)

    override fun menuItemBackground(context: Context) =
        ContextCompat.getColor(context, R.color.blueFrame_light)

    override fun iconBack() = R.drawable.arrow_back
    override fun iconNext() = R.drawable.chevron_right
    override fun loadingIcon() = R.drawable.ic_loading
    override fun appLogo() = R.drawable.app_logo
    override fun appLogoLandscape() = R.drawable.logo_landscape
    override fun indicatorActive(context: Context) =
        ContextCompat.getColor(context, R.color.text_light)

    override fun indicatorInactive(context: Context) =
        ContextCompat.getColor(context, R.color.grayFrame_light)
    override fun textGray(context: Context) =
        ContextCompat.getColor(context, R.color.textGray_light)
    override fun userMenuItem() = R.drawable.person

    override fun favoriteMenuItem() = R.drawable.bookmark

    override fun userManagementMenuItem() = R.drawable.personal_privacy

    override fun feedbackMenuItem() = R.drawable.help
    override fun menuBackground() = R.drawable.round_corner
    override fun bookmarkIcon(): Int = R.drawable.ic_bookmark
    override fun bookmarkFilledIcon(): Int = R.drawable.ic_bookmark_filled
    override fun iconClose(): Int = R.drawable.close
    override fun iconEdit(): Int = R.drawable.ic_edit
    override fun iconLock(): Int = R.drawable.ic_lock
    override fun chipBgSelected(): Int = R.drawable.round_corner_dp16_night
    override fun chipBg(): Int = R.drawable.round_corner_dp16
    override fun iconSearch(): Int = R.drawable.ic_search
    override fun iconRate(): Int = R.drawable.rate
    override fun iconShare(): Int = R.drawable.share
    override fun dividerColorRes(): Int = R.color.icon_light
    override fun iconFilter(): Int = R.drawable.ic_filter
    override fun bottomSheetBg(): Int = R.drawable.bottom_sheet_bg
    override fun dialogBg(): Int = R.drawable.dialog_bg
    override fun filledBtnDisableColor(context: Context): Int =
        ContextCompat.getColor(context, R.color.textGray_light)
    override fun carouselBg(): Int = R.drawable.custom_background_banner
    override fun id(): Int = 0
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is LightTheme) return false
        return this.javaClass.simpleName == other.javaClass.simpleName
    }
}