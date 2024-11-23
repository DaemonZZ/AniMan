package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.UserItemBinding
import com.daemonz.animange.entity.Account

class AccountListAdapter(
    onItemClickListener: OnItemClickListener<Account>,
) : BaseRecyclerAdapter<Account, UserItemBinding>(onItemClickListener) {
    private var displayMode = MODE_CREATED_DATE
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> UserItemBinding
        get() = UserItemBinding::inflate

    override fun bindView(binding: UserItemBinding, item: Account, position: Int) {
        binding.apply {
            title.text = "${item.name} - ${item.region}"
            subTitle.text = item.email
            textDate.text = when (displayMode) {
                MODE_LAST_LOGIN -> item.lastLogin.toString()
                MODE_CREATED_DATE -> item.users.firstOrNull { it.isMainUser }?.createdAt.toString()
                else -> "Unknown"
            }
        }
    }

    fun setDisplayMode(mode: Int) {
        displayMode = mode
    }

    companion object {
        const val MODE_LAST_LOGIN = 0
        const val MODE_CREATED_DATE = 1
    }
}