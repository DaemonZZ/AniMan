package com.daemonz.animange.fragment

import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.MainActivity
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentSettingBinding
import com.daemonz.animange.entity.MenuItem
import com.daemonz.animange.entity.MenuItemFunction
import com.daemonz.animange.entity.UserType
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.AdBannerHandler
import com.daemonz.animange.ui.adapter.MenuAdapter
import com.daemonz.animange.ui.dialog.DeleteDialog
import com.daemonz.animange.util.AdmobConst
import com.daemonz.animange.util.AdmobConstTest
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.loadImageFromStorage
import com.daemonz.animange.viewmodel.LoginViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment :
    BaseFragment<FragmentSettingBinding, LoginViewModel>(FragmentSettingBinding::inflate),
    AdBannerHandler {
    override val viewModel: LoginViewModel by activityViewModels()
    private var adView: AdView? = null
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
                MenuItemFunction.AdminMenu -> findNavController().navigate(
                    SettingsFragmentDirections.actionSettingsFragmentToAdminFragment()
                )
                MenuItemFunction.Language -> findNavController().navigate(
                    SettingsFragmentDirections.actionSettingsFragmentToLanguageFragment()
                )
                MenuItemFunction.Notifications -> findNavController().navigate(
                    SettingsFragmentDirections.actionSettingsFragmentToNotiFragment()
                )
                else -> {
                    showToastNotImplemented()
                }
            }
        }

    override fun setupViews() {
        loadViewState()
        val listItem = mutableListOf(
            MenuItem(
                currentTheme.userMenuItem(),
                getString(R.string.profile),
                menuFunction = MenuItemFunction.AccountInfo
            ),
            MenuItem(
                currentTheme.icNoti(),
                getString(R.string.notification),
                menuFunction = MenuItemFunction.Notifications,
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
//            MenuItem(
//                currentTheme.icLanguage(),
//                getString(R.string.language),
//                menuFunction = MenuItemFunction.Language
//            ),
        )
        if (LoginData.getActiveUser()?.userType == UserType.ADMIN) {
            listItem.add(
                MenuItem(
                    currentTheme.feedbackMenuItem(),
                    getString(R.string.admin_menu),
                    menuFunction = MenuItemFunction.AdminMenu
                ),
            )
        }
        binding.apply {
            adapter = MenuAdapter(onItemClickListener, currentTheme)
            recyclerMenu.adapter = adapter
            adapter?.setData(listItem)
            recyclerMenu.setBackgroundResource(currentTheme.menuBackground())
            textEmail.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            textEmail.text = LoginData.account?.email
            textVer.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            textVer.text = BuildConfig.VERSION_NAME
            btnLogout.setOnClickListener {
                DeleteDialog(
                    title = R.string.log_out,
                    decs = R.string.do_you_want_to_log_out,
                    yes = R.string.log_out,
                    no = R.string.no,
                    onYes = {
                        viewModel.logout(requireContext())
                    },
                    theme = currentTheme
                ).show(childFragmentManager, TAG)
            }
        }
        setupAdView()
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

    override fun loadBanner() {
        ALog.v(TAG, "loadBanner")
        adView?.adUnitId =
            if (BuildConfig.BUILD_TYPE == "release") AdmobConst.BANNER_AD_ADAPTIVE_2 else AdmobConstTest.BANNER_AD_ADAPTIVE
        adView?.setAdSize(adSize)

        val adRequest = AdRequest.Builder().build()

        ALog.v(TAG, "adRequest:isTestDevice: ${adRequest.isTestDevice(requireContext())}")

        adView?.loadAd(adRequest)
    }

    override fun setupAdView() {
        ALog.d(TAG, "setupAdView loadBanner")
        adView = null
        adView = AdView(requireContext())
        binding.adViewContainer.addView(adView)
        binding.adViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
            if ((activity as? MainActivity)?.isReadyToLoadBanner() == true && adView == null) {
                loadBanner()
            }
        }
        loadBanner()
    }

    override fun onResume() {
        super.onResume()
        adView?.resume()
    }

    override fun onPause() {
        super.onPause()
        adView?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        adView?.destroy()
    }
}