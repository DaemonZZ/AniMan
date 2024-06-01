package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentSplashBinding
import com.daemonz.animange.viewmodel.SplashViewModel

class SplashFragment: BaseFragment<FragmentSplashBinding, SplashViewModel>(FragmentSplashBinding::inflate) {
    override val viewModel: SplashViewModel by viewModels<SplashViewModel>()

    override fun setupViews() {
        binding.apply {
            btn.setOnClickListener {
                findNavController().navigate(SplashFragmentDirections.toHomeFragment())
            }
        }
    }

    override fun setupObservers() {
        //
    }

}