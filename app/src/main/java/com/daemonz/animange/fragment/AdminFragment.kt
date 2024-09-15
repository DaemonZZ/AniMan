package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentAdnminBinding
import com.daemonz.animange.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminFragment :
    BaseFragment<FragmentAdnminBinding, AdminViewModel>(FragmentAdnminBinding::inflate) {
    override val viewModel: AdminViewModel by viewModels()

    override fun setupViews() {

    }

    override fun setupObservers() {

    }
}