package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentTab1Binding
import com.daemonz.animange.databinding.FragmentTab5Binding
import com.daemonz.animange.viewmodel.HomeViewModel

class Tab5Fragment : BaseFragment<FragmentTab5Binding,HomeViewModel>(FragmentTab5Binding::inflate) {
    override val mViewModel: HomeViewModel by viewModels()

    override fun setupViews() {
    }

    override fun setupObservers() {
    }
}