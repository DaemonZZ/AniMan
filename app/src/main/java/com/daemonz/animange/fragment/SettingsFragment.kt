package com.daemonz.animange.fragment

import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentSettingBinding
import com.daemonz.animange.entity.MenuItem
import com.daemonz.animange.entity.MenuItemFunction
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.MenuAdapter
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.loadImageFromStorage
import com.daemonz.animange.viewmodel.LoginViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment :
    BaseFragment<FragmentSettingBinding, LoginViewModel>(FragmentSettingBinding::inflate) {
    override val viewModel: LoginViewModel by activityViewModels()

    private var adapter: MenuAdapter? = null
    private val onItemClickListener =
        OnItemClickListener<MenuItem> { item, _ ->
            ALog.i(TAG, "onItemClick: $item")
            when (item.menuFunction) {
                MenuItemFunction.AccountInfo -> findNavController().navigate(
                    SettingsFragmentDirections.actionTab5FragmentToProfileFragment(LoginData.account?.id.toString())
                )

                MenuItemFunction.Favorites -> findNavController().navigate(
                    SettingsFragmentDirections.actionTab5FragmentToFavouritesFragment()
                )

                MenuItemFunction.UserManagement -> findNavController().navigate(
                    SettingsFragmentDirections.actionTab5FragmentToChooseUserFragment()
                )

                MenuItemFunction.FeedBack -> findNavController().navigate(
                    SettingsFragmentDirections.actionSettingsFragmentToSupportFragment()
                )

                else -> {
                    showToastNotImplemented()
                }
            }
        }
    override fun setupViews() {
        loadViewState()
        val listItem = listOf(
            MenuItem(
                currentTheme.userMenuItem(),
                getString(R.string.profile),
                menuFunction = MenuItemFunction.AccountInfo
            ),
            MenuItem(
                currentTheme.favoriteMenuItem(),
                getString(R.string.favourite),
                menuFunction = MenuItemFunction.Favorites
            ),
            MenuItem(
                currentTheme.userManagementMenuItem(),
                getString(R.string.user_management),
                menuFunction = MenuItemFunction.UserManagement
            ),
            MenuItem(
                currentTheme.feedbackMenuItem(),
                getString(R.string.feedback),
                menuFunction = MenuItemFunction.FeedBack
            ),
        )
        binding.apply {
            adapter = MenuAdapter(onItemClickListener, currentTheme)
            recyclerMenu.adapter = adapter
            adapter?.setData(listItem)
            recyclerMenu.setBackgroundResource(currentTheme.menuBackground())
            textEmail.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            textEmail.text = LoginData.account?.email
            textVer.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            textVer.text = BuildConfig.VERSION_NAME
        }
    }

    override fun syncTheme(appTheme: AppTheme) {
        super.syncTheme(appTheme)
        setupViews()
    }

    private fun loadViewState() {
        ALog.d(
            TAG,
            "loadViewState: isloggedin: ${viewModel.isLoggedIn()}  account: ${LoginData.account}"
        )
        binding.apply {
            if (viewModel.isLoggedIn()) {
                val activeUser = LoginData.getActiveUser()
                    ?: LoginData.account?.users?.firstOrNull { it.isMainUser }
                activeUser?.let {
                    ALog.d(TAG, "user: ${it.image}")
                    imgUser.loadImageFromStorage(it.image ?: 1)
                    textUser.text = LoginData.account?.name
                }
            } else {

            }

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
                if (it == null) {
                    lifecycleScope.launch {
                        delay(1000)
                        hideLoadingOverlay()
                        findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToWelcomeFragment())
                    }
                } else {
                    loadViewState()
                    hideLoadingOverlay()
                }
            }
        }
    }
    override fun initData() {

    }

    override fun syncTheme() {
        super.syncTheme()
        binding.apply {

        textUser.setTextColor(currentTheme.firstActivityTextColor(requireContext()))

        }
    }

}