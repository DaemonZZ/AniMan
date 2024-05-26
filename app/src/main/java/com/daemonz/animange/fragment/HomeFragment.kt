package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentHomeBinding
import com.daemonz.animange.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: BaseFragment<FragmentHomeBinding,HomeViewModel>(FragmentHomeBinding::inflate) {
    override val viewModel: HomeViewModel by viewModels()

    override fun setupViews() {
        binding.apply {
            val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            bottomNavigation.setupWithNavController(navController)
            topAppBar.setNavigationOnClickListener {
                // Handle navigation icon press
            }

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
                when(item.itemId) {
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


    override fun setupObservers() {
    }
}