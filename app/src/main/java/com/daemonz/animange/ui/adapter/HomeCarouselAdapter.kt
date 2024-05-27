package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.CarouselItemBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.setImageFromUrl

class HomeCarouselAdapter(onItemClickListener: OnItemClickListener<Item>) :
    BaseRecyclerAdapter<Item, CarouselItemBinding>(onItemClickListener = onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CarouselItemBinding
        get() = CarouselItemBinding::inflate

    override fun bindView(binding: CarouselItemBinding, item: Item, position: Int) {
        ALog.i(TAG, "bindView: $position - item: $item")
        binding.carouselImageView.setImageFromUrl(item.getImageUrl(imgDomain))
    }
}
