package com.daemonz.animange.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentHomeBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.CommonAction
import com.daemonz.animange.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(FragmentHomeBinding::inflate) {
    override val viewModel: HomeViewModel by viewModels()

    private val listFragmentsWithNavbar = listOf(
        R.id.tab1Fragment,
        R.id.tab2Fragment,
        R.id.tab3Fragment,
        R.id.tab4Fragment,
        R.id.tab5Fragment,
    )

    private val navChangeListener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            ALog.i(TAG, "onDestinationChanged: ${destination.id}")
            if (destination.id in listFragmentsWithNavbar) {
                binding.bottomNavigation.visibility = View.VISIBLE
            } else {
                binding.bottomNavigation.visibility = View.GONE
            }
        }

    override fun setupViews() {
        binding.apply {
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            bottomNavigation.setupWithNavController(navController)

            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
//                    R.id.edit -> {
//                        // Handle edit text press
//                        true
//                    }
//                    R.id.favorite -> {
//                        // Handle favorite icon press
//                        true
//                    }
//                    R.id.more -> {
//                        // Handle more item (inside overflow menu) press
//                        true
//                    }
                    else -> false
                }
            }
//            NavigationBarView.OnItemSelectedListener { item ->
//
//                true
//            }
            bottomNavigation.setOnItemReselectedListener { item ->
                when (item.itemId) {
//                    R.id.item_1 -> {
//                        // Respond to navigation item 1 reselection
//                    }
//                    R.id.item_2 -> {
//                        // Respond to navigation item 2 reselection
//                    }
                }
            }
        }
    }

    private fun reloadData() {
        childFragmentManager.fragments.firstOrNull()?.let {
            it.childFragmentManager.fragments.forEach {
                ALog.d(TAG, "reloadData ${it.javaClass.simpleName}")
                (it as? CommonAction)?.onRefresh()
            }
        }
    }
    override fun setupObservers() {
    }

    override fun onResume() {
        super.onResume()
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            .addOnDestinationChangedListener(navChangeListener)
    }

    override fun onPause() {
        super.onPause()
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            .removeOnDestinationChangedListener(navChangeListener)
    }

    private fun showHideToolbar(isShow: Boolean) {
        binding.appBarLayout.apply {
            if (isShow) {
                animate().translationY(0f)
                    .alpha(1f)
                    .setDuration(200)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            super.onAnimationStart(animation)
                            isVisible = true
                        }
                    })
            } else {
                animate().translationY(-this.height.toFloat())
                    .alpha(0f)
                    .setDuration(200)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            isVisible = false
                        }
                    })
            }
        }
    }

    fun toggleToolBarShowing(isShow: Boolean? = null, autoHide: Boolean = false) {
        isShow?.let {
            showHideToolbar(it)
        } ?: kotlin.run {
            showHideToolbar(!binding.appBarLayout.isVisible)
        }
        if (isShow == true && autoHide) {
            binding.appBarLayout.postDelayed({
                showHideToolbar(false)
            }, 3000)
        }
    }

    fun changeToolBarAction(fragment: Fragment) {
        when (fragment) {
            is PlayerFragment -> {
                binding.topAppBar.navigationIcon =
                    ResourcesCompat.getDrawable(resources, R.drawable.arrow_back, null)
                binding.topAppBar.setNavigationOnClickListener {
                    val navController =
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    navController.popBackStack()
                }
                binding.topAppBar.fitsSystemWindows = false
            }

            else -> {
                binding.topAppBar.navigationIcon =
                    ResourcesCompat.getDrawable(resources, R.drawable.app_logo, null)
                binding.topAppBar.setNavigationOnClickListener {
                    //
                }
                binding.topAppBar.fitsSystemWindows = true
            }
        }

    }
}