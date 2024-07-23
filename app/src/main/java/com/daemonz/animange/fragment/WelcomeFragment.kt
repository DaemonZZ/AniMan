package com.daemonz.animange.fragment

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentWelcomeBinding
import com.daemonz.animange.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WelcomeFragment :
    BaseFragment<FragmentWelcomeBinding, LoginViewModel>(FragmentWelcomeBinding::inflate) {
    override val viewModel: LoginViewModel by activityViewModels()
    private var animation: Animation? = null

    override fun setupViews() {
        animation = AnimationUtils.loadAnimation(requireContext(), R.anim.loop_rotate)
        toggleToolBarShowing(false)
        animation?.interpolator = LinearInterpolator()
        animation?.repeatCount = Animation.INFINITE
        animation?.repeatMode = Animation.RESTART
        binding.loader.startAnimation(animation)
        binding.guideText.text = getString(R.string.check_login)
        binding.btnLogin.setOnClickListener {
            viewModel.createSigningLauncher()
        }
    }

    override fun setupObservers() {
        viewModel.account.observe(viewLifecycleOwner) {
            if (it != null) {
                lifecycleScope.launch {
                    loadData()
                }
            }
        }
    }

    override fun initData() {
        super.initData()
        lifecycleScope.launch {
            checkLogin()
        }
    }

    private suspend fun checkLogin() {
        delay(2000)
        binding.apply {
            if (viewModel.isLoggedIn()) {
                loadData()
            } else {
                loader.clearAnimation()
                loader.isVisible = false
                guideText.isVisible = false
                logo.isVisible = true
                btnLogin.isVisible = true
            }
        }
    }

    private suspend fun loadData() {
        binding.apply {
            loader.isVisible = true
            logo.isVisible = false
            guideText.text = getString(R.string.loading_data)
            guideText.isVisible = true
            btnLogin.isVisible = false
            loader.startAnimation(animation)
        }
        delay(2000)
        findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToHomeFragment())
    }


    override fun syncTheme() {
        super.syncTheme()
        binding.loader.setImageResource(currentTheme.loadingIcon())
        binding.guideText.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
        binding.logo.setImageResource(currentTheme.appLogo())
    }

}