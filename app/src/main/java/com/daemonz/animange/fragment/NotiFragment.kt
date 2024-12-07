package com.daemonz.animange.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.MainActivity
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentNotiBinding
import com.daemonz.animange.entity.Notification
import com.daemonz.animange.entity.NotificationType
import com.daemonz.animange.ui.adapter.NotificationAdapter
import com.daemonz.animange.util.NotiCache
import com.daemonz.animange.viewmodel.NotiViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotiFragment :
    BaseFragment<FragmentNotiBinding, NotiViewModel>(FragmentNotiBinding::inflate) {
    override val viewModel: NotiViewModel by viewModels()
    private var adapter: NotificationAdapter? = null


    override fun setupViews() {
        (activity as? MainActivity)?.setTitle(getString(R.string.notification))
        binding.apply {
            adapter = NotificationAdapter(
                onItemClickListener = { item, _ ->
                    viewModel.markNotiAsRead(item)
                    when (item.type) {
                        NotificationType.NEWS -> {

                        }

                        NotificationType.NEW_UPDATE -> {
                            try {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
                                    )
                                )
                            } catch (e: ActivityNotFoundException) {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=$${BuildConfig.APPLICATION_ID}")
                                    )
                                )
                            }
                        }

                        NotificationType.REPLY_FEEDBACK -> {

                        }

                        else -> {

                        }
                    }
                },
                theme = currentTheme
            )
            recycler.adapter = adapter
            adapter?.setData(NotiCache.cachedNotifications)
            textNoComment.isVisible = NotiCache.cachedNotifications.isEmpty()
            textNoComment.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            recycler.isVisible = NotiCache.cachedNotifications.isNotEmpty()
        }
    }

    override fun setupObservers() {

    }

    override fun syncTheme() {
        super.syncTheme()
        setupViews()
    }
}