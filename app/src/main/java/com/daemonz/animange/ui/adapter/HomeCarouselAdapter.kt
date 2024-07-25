package com.daemonz.animange.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.CarouselItemBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.daemonz.animange.util.setImageFromUrl
import com.google.android.material.chip.Chip

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
            textTitle.text = item.name
            textTitle.setTextColor(theme.firstActivityTextColor(root.context))
            btnWatch.setTextColor(theme.firstActivityBackgroundColor(root.context))
            btnWatch.setBackgroundColor(theme.firstActivityIconColor(root.context))
            chipGroup.removeAllViews()
            item.category.distinctBy { it.name }.forEach {
                val chip = Chip(root.context).apply {
                    text = it.name
                    setBackgroundColor(Color.TRANSPARENT)
                    val shape = shapeAppearanceModel.withCornerSize(50f)
                    shapeAppearanceModel = shape
                }
                chipGroup.addView(chip)
            }
        }
    }
}
