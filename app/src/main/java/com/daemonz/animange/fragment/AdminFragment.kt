package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentAdnminBinding
import com.daemonz.animange.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminFragment :
    BaseFragment<FragmentAdnminBinding, AdminViewModel>(FragmentAdnminBinding::inflate) {
    override val viewModel: AdminViewModel by viewModels()

    override fun setupViews() {
        binding.apply {
            cardUsers.setOnClickListener {
                findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToAccountListFragment())
            }
            cardActivities.setOnClickListener {
                findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToActivitiesFragment())
            }
        }
    }

    override fun setupObservers() {
        viewModel.userCount.observe(viewLifecycleOwner) {
            binding.textTotalUsers.text = getString(R.string.total_user, it)
        }
        viewModel.userCountToday.observe(viewLifecycleOwner) {
            binding.textTotalUsersToday.text = getString(R.string.new_user_today, it)
        }
        viewModel.userCountYesterday.observe(viewLifecycleOwner) {
            binding.textTotalUsersYesterday.text = getString(R.string.users_yesterday, it)
        }
        viewModel.userCountThisMonth.observe(viewLifecycleOwner) {
            binding.textTotalUsersThisMonth.text = getString(R.string.users_this_month, it)
        }
        viewModel.userActive.observe(viewLifecycleOwner) {
            binding.recentlyUser.text = getString(R.string.recently_users, it)
        }
    }

    override fun initData() {
        viewModel.getUserCount()
        viewModel.getTotalUsersActiveToday()
    }
}