package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentNotiBinding
import com.daemonz.animange.viewmodel.NotiViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotiFragment :
    BaseFragment<FragmentNotiBinding, NotiViewModel>(FragmentNotiBinding::inflate) {
    override val viewModel: NotiViewModel by viewModels()

    override fun setupViews() {

    }

    override fun setupObservers() {

    }
}