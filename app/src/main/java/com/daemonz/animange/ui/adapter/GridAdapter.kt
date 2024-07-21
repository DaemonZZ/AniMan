package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.R
import com.daemonz.animange.base.BasePagingAdapter
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.CardItemBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.daemonz.animange.util.setImageFromUrl

class GridAdapter(
    private val onItemClickListener: OnItemClickListener<PagingData<Item>>,
    private val theme: AnimanTheme
) : BasePagingAdapter<Item, PagingData<Item>, CardItemBinding>(onItemClickListener, theme) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CardItemBinding
        get() = CardItemBinding::inflate

    override fun bindView(binding: CardItemBinding, item: PagingData<Item>, position: Int) {
        binding.apply {
            imgView.setImageFromUrl(item.data.getImageUrl(imgDomain))
            textTitle.text = item.data.name
            textSubtitle.text = item.data.category.map { it.name }.joinToString(
                binding.root.context.getString(
                    R.string.bullet
                )
            )
            textTitle.setTextColor(theme.firstActivityTextColor(root.context))
            textSubtitle.setTextColor(theme.firstActivityTextColor(root.context))
        }
    }
}