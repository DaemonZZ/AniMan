package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.UserItemBinding
import com.daemonz.animange.entity.Activity

class ActivitiesAdapter(
    onItemClickListener: OnItemClickListener<Activity>,
) : BaseRecyclerAdapter<Activity, UserItemBinding>(onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> UserItemBinding
        get() = UserItemBinding::inflate

    override fun bindView(binding: UserItemBinding, item: Activity, position: Int) {
        binding.apply {
            title.text = "${item.accountName} - ${item.activity?.name}"
            subTitle.text = item.content
            textDate.text = item.time.toString()
        }
    }
}