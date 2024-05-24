package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentTab1Binding
import com.daemonz.animange.databinding.FragmentTab4Binding
import com.daemonz.animange.viewmodel.HomeViewModel

class Tab4Fragment : BaseFragment<FragmentTab4Binding,HomeViewModel>(FragmentTab4Binding::inflate) {
    override val mViewModel: HomeViewModel by viewModels()

    override fun setupViews() {
    }

    override fun setupObservers() {
    }
}