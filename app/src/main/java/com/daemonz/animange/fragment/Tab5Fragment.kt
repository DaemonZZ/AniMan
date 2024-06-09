package com.daemonz.animange.fragment

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentTab5Binding
import com.daemonz.animange.entity.FavouriteItem
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.SettingItem
import com.daemonz.animange.entity.SettingItemType
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.BottomNavigationAction
import com.daemonz.animange.ui.adapter.FavouriteAdapter
import com.daemonz.animange.ui.adapter.SettingAdapter
import com.daemonz.animange.ui.dialog.SearchDialog
import com.daemonz.animange.util.dpToPx
import com.daemonz.animange.viewmodel.HomeViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration

class Tab5Fragment : BaseFragment<FragmentTab5Binding, HomeViewModel>(FragmentTab5Binding::inflate),
    BottomNavigationAction {
    override val viewModel: HomeViewModel by activityViewModels()
    private val onItemClickListener =
        OnItemClickListener<SettingItem> { item, _ -> }
    private var settingAdapter: SettingAdapter? = null

    private val settingItems = mutableListOf<SettingItem>(
        SettingItem(
            icon = R.drawable.ic_home,
            type = SettingItemType.LOGIN
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
        SettingItem(
            icon = R.drawable.ic_home,
            type = SettingItemType.USER
        )
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

    }

    override fun onSearch() {
        SearchDialog { item, index -> navigateToPlayer(item.slug) }.show(
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
        viewModel.getFavourites()
        showLoadingOverlay("getFavourites")
    }

    private fun navigateToPlayer(slug: String) {
        ALog.i(TAG, "navigateToPlayer: $slug")
        findNavController().navigate(Tab5FragmentDirections.actionTab5FragmentToPlayerFragment(item = slug))
    }
}