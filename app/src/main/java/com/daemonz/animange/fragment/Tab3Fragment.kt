package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentTab1Binding
import com.daemonz.animange.databinding.FragmentTab3Binding
import com.daemonz.animange.ui.CommonAction
import com.daemonz.animange.viewmodel.HomeViewModel

class Tab3Fragment : BaseFragment<FragmentTab3Binding, HomeViewModel>(FragmentTab3Binding::inflate),
    CommonAction {
    override val viewModel: HomeViewModel by viewModels()

    override fun setupViews() {
    }

    override fun setupObservers() {
    }

    override fun onRefresh() {
        TODO("Not yet implemented")
    }

    override fun onReSelectBottomNavigationItem(itemId: Int) {
        TODO("Not yet implemented")
    }
}