package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.daemonz.animange.MainActivity
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentSplashBinding
import com.daemonz.animange.util.loadGif
import com.daemonz.animange.util.setImageFromUrl
import com.daemonz.animange.viewmodel.SplashViewModel

class SplashFragment: BaseFragment<FragmentSplashBinding, SplashViewModel>(FragmentSplashBinding::inflate) {
    override val viewModel: SplashViewModel by viewModels<SplashViewModel>()

    override fun setupViews() {
        binding.apply {
            (activity as? MainActivity)?.toggleToolBarShowing(false, autoHide = false)
            loading.loadGif(R.drawable.loading)
            loading.postDelayed({
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToTab1Fragment())
            }, 3000)
        }

    }
    override fun setupObservers() {
        //
    }

}