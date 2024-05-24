package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentTab1Binding
import com.daemonz.animange.databinding.FragmentTab3Binding
import com.daemonz.animange.viewmodel.HomeViewModel

class Tab3Fragment : BaseFragment<FragmentTab3Binding,HomeViewModel>(FragmentTab3Binding::inflate) {
    override val mViewModel: HomeViewModel by viewModels()

    override fun setupViews() {
    }

    override fun setupObservers() {
    }
}