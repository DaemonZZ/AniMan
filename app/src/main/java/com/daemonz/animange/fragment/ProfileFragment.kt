package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentEditProfileBinding
import com.daemonz.animange.viewmodel.HomeViewModel

class ProfileFragment :
    BaseFragment<FragmentEditProfileBinding, HomeViewModel>(FragmentEditProfileBinding::inflate) {
    override val viewModel: HomeViewModel by viewModels()

    override fun setupViews() {

    }

    override fun setupObservers() {

    }
}