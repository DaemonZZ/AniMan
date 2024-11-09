package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentLanguageBinding
import com.daemonz.animange.viewmodel.LanguageViewModel
import com.dolatkia.animatedThemeManager.AppTheme

class LanguageFragment :
    BaseFragment<FragmentLanguageBinding, LanguageViewModel>(FragmentLanguageBinding::inflate) {
    override val viewModel: LanguageViewModel by viewModels()

    override fun setupViews() {

    }

    override fun setupObservers() {

    }

    override fun syncTheme(appTheme: AppTheme) {
        super.syncTheme(appTheme)

    }
}