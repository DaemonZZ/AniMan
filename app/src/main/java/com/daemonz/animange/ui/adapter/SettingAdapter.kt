package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.MenuItemCommonBinding
import com.daemonz.animange.databinding.MenuItemLoginBinding
import com.daemonz.animange.databinding.MenuItemUserBinding
import com.daemonz.animange.databinding.MenuTitleItemBinding
import com.daemonz.animange.entity.Account
import com.daemonz.animange.entity.SettingItem


class SettingAdapter(
    onItemClickListener: OnItemClickListener<SettingItem>,
    private val account: Account? = null
) : BaseRecyclerAdapter<SettingItem, ViewBinding>(onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding
        get() = MenuItemCommonBinding::inflate

    override fun bindView(binding: ViewBinding, item: SettingItem, position: Int) {
        when (binding) {
            is MenuItemCommonBinding -> {
                if (position == 2) binding.divider.isVisible = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewBinding> {
        val holder = when (viewType) {
            SettingViewType.LOGIN.value -> BaseViewHolder<ViewBinding>(
                MenuItemLoginBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            SettingViewType.ACCOUNT.value -> BaseViewHolder<ViewBinding>(
                MenuItemUserBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            SettingViewType.MENU_TITLE.value -> BaseViewHolder<ViewBinding>(
                MenuTitleItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            else -> BaseViewHolder<ViewBinding>(
                MenuItemCommonBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 && account == null -> {
                SettingViewType.LOGIN.value
            }

            position == 0 -> {
                SettingViewType.ACCOUNT.value
            }

            position == 1 -> {
                SettingViewType.MENU_TITLE.value
            }

            else -> {
                SettingViewType.MENU_ITEM.value
            }
        }
    }
}

enum class SettingViewType(val value: Int) {
    LOGIN(0),
    ACCOUNT(1),
    MENU_ITEM(2),
    MENU_TITLE(3)
}