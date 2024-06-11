package com.daemonz.animange.fragment

import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentTab5Binding
import com.daemonz.animange.entity.SettingItem
import com.daemonz.animange.entity.SettingItemType
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.BottomNavigationAction
import com.daemonz.animange.ui.adapter.SettingAdapter
import com.daemonz.animange.ui.dialog.SearchDialog
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Tab5Fragment :
    BaseFragment<FragmentTab5Binding, LoginViewModel>(FragmentTab5Binding::inflate),
    BottomNavigationAction {
    override val viewModel: LoginViewModel by activityViewModels()
    private val onItemClickListener =
        OnItemClickListener<SettingItem> { item, pos ->
            when (pos) {
                SettingItemType.LOGIN.pos, SettingItemType.USER.pos -> {
                    if (LoginData.account == null) {
                        viewModel.createSigningLauncher()
                    } else {

                    }
                }
            }
        }
    private var settingAdapter: SettingAdapter? = null

    private val settingItems = mutableListOf(
        SettingItem(
            icon = R.drawable.ic_home,
            type = SettingItemType.LOGIN
        ),
        SettingItem(
            icon = R.drawable.ic_home,
            type = SettingItemType.USER,
            isShow = false
        ),
        SettingItem(
            icon = R.drawable.ic_home,
            type = SettingItemType.TITLE
        ),
        SettingItem(
            icon = R.drawable.ic_home,
            type = SettingItemType.ACCOUNT_INFO
        ),
        SettingItem(
            icon = R.drawable.ic_home,
            type = SettingItemType.FAVOURITE
        ),
        SettingItem(
            icon = R.drawable.ic_home,
            type = SettingItemType.FEEDBACK
        ),
        SettingItem(
            icon = R.drawable.ic_home,
            type = SettingItemType.LOGOUT
        ),
    )

    override fun setupViews() {
        binding.apply {
            toggleToolBarShowing(false)
            settingMenu.layoutManager = LinearLayoutManager(requireContext())
            settingAdapter = SettingAdapter(onItemClickListener, null)
            settingMenu.adapter = settingAdapter
            settingAdapter?.setData(settingItems)
        }

    }

    override fun setupObservers() {
        viewModel.error.observe(viewLifecycleOwner) {
            ALog.e(TAG, "setupObservers: $it")
            if (it != null) {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSearch() {
        SearchDialog { item, _ -> navigateToPlayer(item.slug) }.show(
            childFragmentManager,
            "SearchDialog"
        )
    }

    override fun onRefresh() {
        TODO("Not yet implemented")
    }

    override fun onReSelectBottomNavigationItem(itemId: Int) {
        TODO("Not yet implemented")
    }

    override fun initData() {

    }

    private fun navigateToPlayer(slug: String) {
        ALog.i(TAG, "navigateToPlayer: $slug")
        findNavController().navigate(Tab5FragmentDirections.actionTab5FragmentToPlayerFragment(item = slug))
    }
}