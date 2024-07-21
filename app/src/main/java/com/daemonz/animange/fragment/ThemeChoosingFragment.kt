package com.daemonz.animange.fragment

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.daemonz.animange.MainActivity
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentMenuBinding
import com.daemonz.animange.entity.MenuItem
import com.daemonz.animange.entity.MenuItemType
import com.daemonz.animange.ui.adapter.MenuAdapter
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.daemonz.animange.ui.thememanager.DarkTheme
import com.daemonz.animange.ui.thememanager.LightTheme
import com.daemonz.animange.util.NIGHT_MODE_KEY
import com.daemonz.animange.util.SharePreferenceManager
import com.daemonz.animange.util.AppThemeManager
import com.daemonz.animange.viewmodel.ThemeViewModel
import com.dolatkia.animatedThemeManager.ThemeManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ThemeChoosingFragment :
    BaseFragment<FragmentMenuBinding, ThemeViewModel>(FragmentMenuBinding::inflate) {
    override val viewModel: ThemeViewModel by viewModels()

    @Inject
    lateinit var sharePreferenceManager: SharePreferenceManager
    private var adapter: MenuAdapter? = null

    override fun setupViews() {
        binding.recycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = MenuAdapter(
            onItemClickListener = { item, _ -> },
            theme = currentTheme
        )
        binding.recycler.adapter = adapter
        val data = (0..<AppThemeManager.themes.size).map {
            MenuItem(it, "Theme ${it + 1}", type = MenuItemType.Theme)
        }
        adapter?.setData(data)
        val nightMode = sharePreferenceManager.getBoolean(NIGHT_MODE_KEY, false)
        binding.switchLayout.fitsSystemWindows = true
        binding.dayNightSwitch.isVisible = true
        binding.dayNightSwitch.setIsNight(nightMode, false)
        binding.dayNightSwitch.setListener { isNightMode ->
            binding.root.postDelayed({
                val theme: AnimanTheme = if (isNightMode) {
                    DarkTheme()
                } else {
                    LightTheme()
                }
                sharePreferenceManager.setBoolean(NIGHT_MODE_KEY, isNightMode)
                ThemeManager.instance.reverseChangeTheme(
                    theme,
                    binding.dayNightSwitch
                )
            }, 500L)
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun setupObservers() {
        //
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setTitle(getString(R.string.theme))
    }
}