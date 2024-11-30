package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentNotiBinding
import com.daemonz.animange.entity.Notification
import com.daemonz.animange.ui.adapter.NotificationAdapter
import com.daemonz.animange.viewmodel.NotiViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotiFragment :
    BaseFragment<FragmentNotiBinding, NotiViewModel>(FragmentNotiBinding::inflate) {
    override val viewModel: NotiViewModel by viewModels()
    private var adapter: NotificationAdapter? = null
    val dummyNoti = listOf(
        Notification(
            title = "Notification 1 2 3 4 5 6",
        ),
        Notification(
            title = "Notification 1 2 3 4 5 6",
        ),
        Notification(
            title = "Notification 1 2 3 4 5 6",
        ),
        Notification(
            title = "Notification 1 2 3 4 5 6",
            isRead = true
        ),
        Notification(
            title = "Notification 1 2 3 4 5 6",
        ),
        Notification(
            title = "Notification 1 2 3 4 5 6",
        ),
        Notification(
            title = "Notification 1 2 3 4 5 6",
        ),
        Notification(
            title = "Notification 1 2 3 4 5 6",
        ),
        Notification(
            title = "Notification 1 2 3 4 5 6",
        ),
        Notification(
            title = "Notification 1 2 3 4 5 6",
        ),
    )

    override fun setupViews() {
        binding.apply {
            adapter = NotificationAdapter(
                onItemClickListener = { _, _ -> },
                theme = currentTheme
            )
            recycler.adapter = adapter
            adapter?.setData(dummyNoti)
        }
    }

    override fun setupObservers() {

    }

    override fun syncTheme() {
        super.syncTheme()
        setupViews()
    }
}