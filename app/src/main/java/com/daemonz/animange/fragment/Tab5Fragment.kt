package com.daemonz.animange.fragment

import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentSettingBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.BottomNavigationAction
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.loadImageFromStorage
import com.daemonz.animange.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Tab5Fragment :
    BaseFragment<FragmentSettingBinding, LoginViewModel>(FragmentSettingBinding::inflate),
    BottomNavigationAction {
    override val viewModel: LoginViewModel by activityViewModels()


    override fun setupViews() {
        loadViewState()
        binding.apply {
            profile.textTitle.text = getString(R.string.profile)
            profile.icon.setImageResource(R.drawable.ic_profile)
            profile.root.setOnClickListener {
                if (LoginData.account == null) {
                    Toast.makeText(
                        requireContext(),
                        R.string.user_not_logged_in,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    LoginData.account?.id?.let {
                        findNavController().navigate(
                            Tab5FragmentDirections.actionTab5FragmentToProfileFragment(
                                it
                            )
                        )
                    }
                }

            }
            favourite.textTitle.text = getString(R.string.favourite)
            favourite.icon.setImageResource(R.drawable.ic_favourite)
            favourite.root.setOnClickListener {
                if (viewModel.isLoggedIn()) {
                    findNavController().navigate(Tab5FragmentDirections.actionTab5FragmentToFavouritesFragment())
                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.user_not_logged_in,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            feedback.textTitle.text = getString(R.string.theme)
            feedback.icon.setImageResource(R.drawable.ic_theme)
            feedback.root.setOnClickListener {
                findNavController().navigate(Tab5FragmentDirections.actionTab5FragmentToThemeFragment())
            }
            support.textTitle.text = getString(R.string.support)
            support.icon.setImageResource(R.drawable.ic_support)
            support.root.setOnClickListener {
                showToastNotImplemented()
            }
            logout.textTitle.text = getString(R.string.logout)
            logout.root.setOnClickListener {
                viewModel.logout(requireContext())
                showLoadingOverlay("logout")
            }
        }
    }

    private fun loadViewState() {
        ALog.d(
            TAG,
            "loadViewState: isloggedin: ${viewModel.isLoggedIn()}  account: ${LoginData.account}"
        )
        binding.apply {
            if (viewModel.isLoggedIn()) {
                layoutLogin.isVisible = false
                groupAccount.visibility = View.VISIBLE
                val activeUser = LoginData.getActiveUser()
                    ?: LoginData.account?.users?.firstOrNull { it.isMainUser }
                activeUser?.let {
                    ALog.d(TAG, "user: ${it.image}")
                    imgUser.loadImageFromStorage(it.image ?: 1)
                    textUser.text = LoginData.account?.name
                    imgUser.setOnClickListener {
                        findNavController().navigate(Tab5FragmentDirections.actionTab5FragmentToChooseUserFragment())
                    }
                }
            } else {
                layoutLogin.isVisible = true
                groupAccount.visibility = View.INVISIBLE
                layoutLogin.setOnClickListener {
                    viewModel.createSigningLauncher()
                }
            }
            logout.root.isVisible = viewModel.isLoggedIn()
        }
    }

    override fun setupObservers() {
        viewModel.apply {
            error.observe(viewLifecycleOwner) {
                ALog.e(TAG, "setupObservers: $it")
                if (it != null) {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
            account.observe(viewLifecycleOwner) {
                loadViewState()
                hideLoadingOverlay("logout")
            }
        }
    }

    override fun onSearch() {
        //
    }

    override fun onRefresh() {
        TODO("Not yet implemented")
    }

    override fun onReSelectBottomNavigationItem(itemId: Int) {
        TODO("Not yet implemented")
    }

    override fun initData() {

    }

}