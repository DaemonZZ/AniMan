package com.daemonz.animange.fragment

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.daemonz.animange.MainActivity
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentMenuBinding
import com.daemonz.animange.entity.MenuItem
import com.daemonz.animange.entity.MenuItemType
import com.daemonz.animange.ui.adapter.MenuAdapter
import com.daemonz.animange.util.NIGHT_MODE_KEY
import com.daemonz.animange.util.SharePreferenceManager
import com.daemonz.animange.util.ThemeManager
import com.daemonz.animange.util.getToolbarHeight
import com.daemonz.animange.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ThemeFragment :
    BaseFragment<FragmentMenuBinding, ThemeViewModel>(FragmentMenuBinding::inflate) {
    override val viewModel: ThemeViewModel by viewModels()

    @Inject
    lateinit var sharePreferenceManager: SharePreferenceManager
    private var adapter: MenuAdapter? = null

    override fun setupViews() {
        binding.recycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = MenuAdapter(
            onItemClickListener = { item, _ ->
                ThemeManager.changeTheme(
                    requireContext().applicationContext,
                    requireActivity(),
                    sharePreferenceManager,
                    item.icon
                )
            }
        )
        binding.recycler.adapter = adapter
        val data = (0..<ThemeManager.themes.size).map {
            MenuItem(it, "Theme ${it + 1}", type = MenuItemType.Theme)
        }
        adapter?.setData(data)
        val nightMode = sharePreferenceManager.getBoolean(NIGHT_MODE_KEY, false)
        binding.switchLayout.fitsSystemWindows = true
        binding.dayNightSwitch.isVisible = true
        binding.dayNightSwitch.setIsNight(nightMode, false)
        binding.dayNightSwitch.setListener { isNightMode ->
            binding.root.postDelayed({
                ThemeManager.setNightMode(isNightMode)
                sharePreferenceManager.setBoolean(NIGHT_MODE_KEY, isNightMode)
            }, 1000L)
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