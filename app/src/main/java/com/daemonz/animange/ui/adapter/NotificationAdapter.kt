package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.core.view.isVisible
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.MenuItemCommonBinding
import com.daemonz.animange.databinding.NotiItemBinding
import com.daemonz.animange.entity.Notification
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.daemonz.animange.util.LoginData
import com.google.android.material.badge.ExperimentalBadgeUtils

class NotificationAdapter(
    private val onItemClickListener: OnItemClickListener<Notification>,
    private val theme: AnimanTheme
) : BaseRecyclerAdapter<Notification, NotiItemBinding>(onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> NotiItemBinding
        get() = NotiItemBinding::inflate

    @OptIn(ExperimentalBadgeUtils::class)
    override fun bindView(binding: NotiItemBinding, item: Notification, position: Int) {
        binding.apply {
            root.setBackgroundColor(theme.menuItemBackground(root.context))
            icon.setImageResource(theme.icNoti())
            textTitle.setTextColor(theme.firstActivityTextColor(root.context))
            textDesc.setTextColor(theme.firstActivityTextColor(root.context))
            divider.dividerColor = theme.firstActivityIconColor(root.context)
            textTitle.text = item.getTitle(LoginData.account?.region.toString())
            dot.isVisible = !item.isRead
            iconEnd.setImageResource(theme.iconNext())
            textDesc.text = item.getMessage(LoginData.account?.region.toString())
        }
    }
}
