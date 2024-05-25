package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.CarouselItemBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.setImageFromUrl

class HomeCarouselAdapter(onItemClickListener: OnItemClickListener) :
    BaseRecyclerAdapter<String, CarouselItemBinding>(onItemClickListener = onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CarouselItemBinding
        get() = CarouselItemBinding::inflate

    override fun bindView(binding: CarouselItemBinding, item: String, position: Int) {
        ALog.i(TAG, "bindView: $position - item: $item")
        binding.carouselImageView.setImageFromUrl(item)
    }
}
