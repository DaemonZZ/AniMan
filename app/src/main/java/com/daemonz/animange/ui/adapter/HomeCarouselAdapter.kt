package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.daemonz.animange.R
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
    onItemClickListener = onItemClickListener
) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CarouselItemBinding
        get() = CarouselItemBinding::inflate

    override fun bindView(binding: CarouselItemBinding, item: Item, position: Int) {
        binding.apply {
            carouselImageView.setImageFromUrl(item.getImageUrl(imgDomain))
            btnWatch.setOnClickListener { onItemClickListener.onItemClick(item, position) }
            textTitle.text = item.name
            textTitle.setTextColor(theme.firstActivityTextColor(root.context))
            rating.setTextColor(theme.firstActivityTextColor(root.context))
            textCate.setTextColor(theme.firstActivityTextColor(root.context))
            gradientView.setBackgroundResource(theme.carouselBg())
            btnWatch.setTextColor(theme.firstActivityBackgroundColor(root.context))
            btnWatch.setBackgroundColor(theme.firstActivityIconColor(root.context))
            val cate =
                item.category.joinToString(root.context.getString(R.string.bullet)) { it.name }
            textCate.text = cate
        }
    }
    override fun setupLayout(binding: CarouselItemBinding, parent: ViewGroup) {
//        //set item height according to screen size
//        val lp = binding.root.layoutParams
//        lp.height = (parent.height * 0.95f).toInt()
//        binding.root.layoutParams = lp
    }
}
