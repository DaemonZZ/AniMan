package com.daemonz.animange.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.CardItemBinding
import com.daemonz.animange.databinding.CarouselItemBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.daemonz.animange.util.dpToPx
import com.daemonz.animange.util.setImageFromUrl
import com.google.android.material.button.MaterialButton

class HomeCarouselAdapter(
    private val onItemClickListener: OnItemClickListener<Item>,
    private val theme: AnimanTheme
) : BaseRecyclerAdapter<Item, CarouselItemBinding>(
    onItemClickListener = onItemClickListener
) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CarouselItemBinding
        get() = CarouselItemBinding::inflate

    override fun bindView(binding: CarouselItemBinding, item: Item, position: Int) {
        binding.apply {
            carouselImageView.setImageFromUrl(item.getImageUrl(imgDomain))
            btnWatch.setOnClickListener { onItemClickListener.onItemClick(item, position) }
            textTitle.text = item.name
            btnWatch.setTextColor(theme.firstActivityBackgroundColor(root.context))
            btnWatch.setBackgroundColor(theme.firstActivityIconColor(root.context))
            chipGroup.removeAllViews()
            item.category.distinctBy { it.name }.take(2).forEach {
                val chip = MaterialButton(root.context).apply {
                    text = it.name
                    strokeColor = ColorStateList.valueOf(theme.textGray(root.context))
                    strokeWidth = root.context.dpToPx(2)
                    textSize = 12f
                    setBackgroundColor(Color.TRANSPARENT)
                    setTextColor(ColorStateList.valueOf(theme.firstActivityIconColor(root.context)))
                    val shape = shapeAppearanceModel.withCornerSize(50f)
                    shapeAppearanceModel = shape
                }
                chipGroup.addView(chip)
            }
        }
    }
    override fun setupLayout(binding: CarouselItemBinding, parent: ViewGroup) {
//        //set item height according to screen size
//        val lp = binding.root.layoutParams
//        lp.height = (parent.height * 0.95f).toInt()
//        binding.root.layoutParams = lp
    }
}
