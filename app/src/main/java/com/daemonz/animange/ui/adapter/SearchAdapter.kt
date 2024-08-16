package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.base.BasePagingAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.CardItemBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.daemonz.animange.util.setImageFromUrl

class SearchAdapter(
    private val onItemClickListener: OnItemClickListener<PagingData<Item>>,
    private val theme: AnimanTheme
) : BasePagingAdapter<Item, PagingData<Item>, CardItemBinding>(
    onItemClickListener,
    theme
) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CardItemBinding
        get() = CardItemBinding::inflate

    override fun bindView(
        binding: CardItemBinding,
        item: PagingData<Item>,
        position: Int
    ) {
        binding.apply {
            val img =
                if (imgDomain.isEmpty()) item.data.thumbUrl else item.data.getImageUrl(imgDomain)
            imgView.setImageFromUrl(img)
            imgView.setOnClickListener {
                onItemClickListener.onItemClick(item, position)
            }
            textTitle.text = item.data.name
            textTitle.setTextColor(theme.firstActivityTextColor(root.context))
            textRate.setTextColor(theme.firstActivityTextColor(root.context))
            root.setOnClickListener {
                onItemClickListener.onItemClick(item, position)
            }
        }
    }

    override fun setupLayout(binding: CardItemBinding, parent: ViewGroup) {
        val lp = binding.root.layoutParams
        lp.height = (parent.height / 2.5).toInt()
        binding.root.layoutParams = lp
    }
}