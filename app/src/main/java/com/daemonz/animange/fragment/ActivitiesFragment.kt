package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentUserListBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.ActivitiesAdapter
import com.daemonz.animange.viewmodel.ActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivitiesFragment :
    BaseFragment<FragmentUserListBinding, ActivityViewModel>(FragmentUserListBinding::inflate) {
    override val viewModel: ActivityViewModel by viewModels()
    private var adapter: ActivitiesAdapter? = null
    override fun setupViews() {
        binding.apply {
            adapter = ActivitiesAdapter(onItemClickListener = { _, _ -> })
            recycler.adapter = adapter
        }
    }

    override fun setupObservers() {
        viewModel.activities.observe(viewLifecycleOwner) { activities ->
            ALog.d(TAG, "Account: ${activities.size}")
            adapter?.setData(activities.sortedByDescending { it.time })
        }
    }

    override fun initData() {
        viewModel.getTotalUsersActiveToday()
    }
}