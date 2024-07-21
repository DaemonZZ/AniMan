package com.daemonz.animange.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.daemonz.animange.ui.thememanager.DarkTheme
import com.daemonz.animange.ui.thememanager.LightTheme
import com.daemonz.animange.util.AppThemeManager
import com.daemonz.animange.util.NIGHT_MODE_KEY
import com.daemonz.animange.util.SharePreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseDialog : DialogFragment() {
    protected var currentTheme: AnimanTheme = LightTheme()

    @Inject
    lateinit var sharePreferenceManager: SharePreferenceManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nightMode = sharePreferenceManager.getBoolean(NIGHT_MODE_KEY, false)
        currentTheme = if (nightMode) DarkTheme() else LightTheme()
    }
}