package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentUserListBinding
import com.daemonz.animange.viewmodel.AccountListViewModel

class AccountListFragment :
    BaseFragment<FragmentUserListBinding, AccountListViewModel>(FragmentUserListBinding::inflate) {
    override val viewModel: AccountListViewModel by viewModels()

    override fun setupViews() {
    }

    override fun setupObservers() {
    }
}