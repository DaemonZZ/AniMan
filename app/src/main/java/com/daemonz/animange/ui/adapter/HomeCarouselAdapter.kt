package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.CarouselItemBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.daemonz.animange.util.setImageFromUrl

class HomeCarouselAdapter(
    private val onItemClickListener: OnItemClickListener<Item>,
    private val theme: AnimanTheme
) : BaseRecyclerAdapter<Item, CarouselItemBinding>(
    onItemClickListener = onItemClickListener,
    theme = theme
) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CarouselItemBinding
        get() = CarouselItemBinding::inflate

    override fun bindView(binding: CarouselItemBinding, item: Item, position: Int) {
        binding.apply {
            carouselImageView.setImageFromUrl(item.getImageUrl(imgDomain))
            btnWatch.setOnClickListener { onItemClickListener.onItemClick(item, position) }
            textYear.text = item.year
            textCountry.text = item.country.firstOrNull()?.name ?: item.category.firstOrNull()?.name
            textEpisode.text = item.episodeCurrent
            textTitle.text = item.name
            textYear.setTextColor(theme.firstActivityTextColor(root.context))
            textCountry.setTextColor(theme.firstActivityTextColor(root.context))
            textEpisode.setTextColor(theme.firstActivityTextColor(root.context))
            textTitle.setTextColor(theme.firstActivityTextColor(root.context))
            btnWatch.setTextColor(theme.firstActivityBackgroundColor(root.context))
            btnWatch.setBackgroundColor(theme.firstActivityIconColor(root.context))
        }
    }
}
