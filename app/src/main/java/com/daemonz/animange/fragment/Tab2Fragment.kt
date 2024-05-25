package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentTab1Binding
import com.daemonz.animange.databinding.FragmentTab2Binding
import com.daemonz.animange.viewmodel.HomeViewModel

class Tab2Fragment : BaseFragment<FragmentTab2Binding,HomeViewModel>(FragmentTab2Binding::inflate) {
    override val viewModel: HomeViewModel by viewModels()

    override fun setupViews() {
    }

    override fun setupObservers() {
    }
}