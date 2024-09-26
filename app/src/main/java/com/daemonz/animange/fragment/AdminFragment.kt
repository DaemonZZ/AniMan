package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
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
        viewModel.getUserCount()
    }

    override fun setupObservers() {
        viewModel.userCount.observe(viewLifecycleOwner) {
            binding.textTotalUsers.text = getString(R.string.total_user, it)
        }
    }
}