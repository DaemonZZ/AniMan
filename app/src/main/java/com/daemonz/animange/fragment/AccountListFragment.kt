package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentUserListBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.viewmodel.AccountListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountListFragment :
    BaseFragment<FragmentUserListBinding, AccountListViewModel>(FragmentUserListBinding::inflate) {
    override val viewModel: AccountListViewModel by viewModels()

    override fun setupViews() {
    }

    override fun setupObservers() {
        viewModel.accounts.observe(viewLifecycleOwner) { accounts ->
            ALog.d(TAG, "Account: ${accounts.size}")
        }
    }

    override fun initData() {
        viewModel.getAccount()
    }
}