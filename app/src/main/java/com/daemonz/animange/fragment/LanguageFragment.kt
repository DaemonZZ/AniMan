package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.MainActivity
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentLanguageBinding
import com.daemonz.animange.entity.Language
import com.daemonz.animange.ui.adapter.LanguageAdapter
import com.daemonz.animange.viewmodel.LanguageViewModel
import com.dolatkia.animatedThemeManager.AppTheme

class LanguageFragment :
    BaseFragment<FragmentLanguageBinding, LanguageViewModel>(FragmentLanguageBinding::inflate) {
    override val viewModel: LanguageViewModel by viewModels()
    private var adapter: LanguageAdapter? = null

    override fun setupViews() {
        val langList = listOf(
            Language("en"),
            Language("vi"),
        )
        binding.apply {
            (activity as? MainActivity)?.setTitle(getString(R.string.language))
            adapter = LanguageAdapter(
                onItemClickListener = { _, item ->
//                    viewModel.changeLanguage(item.id)
                }
            )
            recycler.adapter = adapter
            adapter?.setData(langList)
        }
    }

    override fun setupObservers() {

    }

    override fun syncTheme(appTheme: AppTheme) {
        super.syncTheme(appTheme)

    }
}