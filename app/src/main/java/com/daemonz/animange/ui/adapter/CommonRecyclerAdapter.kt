package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.CommonRecyclerItemBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.setImageFromUrl

class CommonRecyclerAdapter(private val onItemClickListener: OnItemClickListener<Item>) :
    BaseRecyclerAdapter<Item, CommonRecyclerItemBinding>(onItemClickListener = onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CommonRecyclerItemBinding
        get() = CommonRecyclerItemBinding::inflate

    override fun bindView(binding: CommonRecyclerItemBinding, item: Item, position: Int) {
        binding.apply {
            carouselImageView.setImageFromUrl(item.getImageUrl(imgDomain))
            title.text = item.name
        }
    }
}