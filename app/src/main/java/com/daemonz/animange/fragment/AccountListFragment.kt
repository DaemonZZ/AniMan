package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentUserListBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.AccountListAdapter
import com.daemonz.animange.viewmodel.AccountListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountListFragment :
    BaseFragment<FragmentUserListBinding, AccountListViewModel>(FragmentUserListBinding::inflate) {
    override val viewModel: AccountListViewModel by viewModels()
    private var adapter: AccountListAdapter? = null
    override fun setupViews() {
        binding.apply {
            adapter = AccountListAdapter(onItemClickListener = { _, _ -> })
            recycler.adapter = adapter
        }
    }

    override fun setupObservers() {
        viewModel.accounts.observe(viewLifecycleOwner) { accounts ->
            ALog.d(TAG, "Account: ${accounts.size}")
            adapter?.setData(accounts.sortedByDescending { it.lastLogin })
        }
    }

    override fun initData() {
        viewModel.getAccount()
    }
}