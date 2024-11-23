package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentUserListBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.AccountListAdapter
import com.daemonz.animange.ui.adapter.ActivitiesAdapter
import com.daemonz.animange.viewmodel.ActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivitiesFragment :
    BaseFragment<FragmentUserListBinding, ActivityViewModel>(FragmentUserListBinding::inflate) {
    override val viewModel: ActivityViewModel by viewModels()

    //    private var adapter: ActivitiesAdapter? = null
    private var adapter: AccountListAdapter? = null
    override fun setupViews() {
        binding.apply {
//            adapter = ActivitiesAdapter(onItemClickListener = { _, _ -> })
            adapter = AccountListAdapter(onItemClickListener = { _, _ -> }).apply {
                setDisplayMode(AccountListAdapter.MODE_LAST_LOGIN)
            }
            recycler.adapter = adapter
        }
    }

    override fun setupObservers() {
//        viewModel.activities.observe(viewLifecycleOwner) { activities ->
//            ALog.d(TAG, "Account: ${activities.size}")
//            adapter?.setData(activities.sortedByDescending { it.time })
//        }
        viewModel.accounts.observe(viewLifecycleOwner) { accounts ->
            ALog.d(TAG, "Account: ${accounts.size}")
            adapter?.setData(accounts.sortedByDescending { it.lastLogin })
        }
    }

    override fun initData() {
//        viewModel.getTotalUsersActiveToday()
        viewModel.getActiveUsersIn(24)
    }
}