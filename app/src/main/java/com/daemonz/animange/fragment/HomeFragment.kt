package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentHomeBinding
import com.daemonz.animange.viewmodel.HomeViewModel

class HomeFragment: BaseFragment<FragmentHomeBinding,HomeViewModel>(FragmentHomeBinding::inflate) {
    override val mViewModel: HomeViewModel by viewModels<HomeViewModel>()

    override fun setupViews() {
    }

    override fun setupObservers() {
    }
}