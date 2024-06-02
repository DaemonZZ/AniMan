package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.daemonz.animange.MainActivity
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentTab1Binding
import com.daemonz.animange.databinding.FragmentTab2Binding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.BottomNavigationAction
import com.daemonz.animange.ui.CommonAction
import com.daemonz.animange.viewmodel.HomeViewModel

class Tab2Fragment : BaseFragment<FragmentTab2Binding, HomeViewModel>(FragmentTab2Binding::inflate),
    BottomNavigationAction {
    override val viewModel: HomeViewModel by viewModels()

    private val onItemClickListener = object : OnItemClickListener<Item> {
        override fun onItemClick(item: Item, index: Int) {
            ALog.i(TAG, "onItemClick: $index, status: ${item.status}")
            navigateToPlayer(item)
        }
    }

    override fun setupViews() {
        (activity as? MainActivity)?.toggleToolBarShowing(isShow = true, autoHide = false)
    }

    override fun setupObservers() {
    }

    override fun onSearch() {
        TODO("Not yet implemented")
    }

    override fun onRefresh() {
        TODO("Not yet implemented")
    }

    override fun onReSelectBottomNavigationItem(itemId: Int) {
        TODO("Not yet implemented")
    }

    private fun navigateToPlayer(item: Item) {
        ALog.i(TAG, "navigateToPlayer: $item")
        findNavController().navigate(Tab2FragmentDirections.actionTab2FragmentToPlayerFragment(item = item.slug))
    }
}