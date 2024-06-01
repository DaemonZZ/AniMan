package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.CarouselItemBinding
import com.daemonz.animange.databinding.FilmCarouselItemBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.setImageFromUrl

class FilmCarouselAdapter(private val onItemClickListener: OnItemClickListener<Item>) :
    BaseRecyclerAdapter<Item, FilmCarouselItemBinding>(onItemClickListener = onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FilmCarouselItemBinding
        get() = FilmCarouselItemBinding::inflate

    override fun bindView(binding: FilmCarouselItemBinding, item: Item, position: Int) {
        ALog.i(TAG, "bindView: $position - item: $item")
        binding.apply {
            carouselImageView.setImageFromUrl(item.getImageUrl(imgDomain))
            title.text = item.name
        }
    }
}
