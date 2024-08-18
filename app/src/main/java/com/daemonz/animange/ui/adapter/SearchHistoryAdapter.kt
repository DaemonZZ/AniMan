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
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.daemonz.animange.util.setImageFromUrl

class SearchHistoryAdapter(
    private val onItemClickListener: OnItemClickListener<Item>,
    private val theme: AnimanTheme
) : BaseRecyclerAdapter<Item, CardItemBinding>(
    onItemClickListener
) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CardItemBinding
        get() = CardItemBinding::inflate

    override fun bindView(
        binding: CardItemBinding,
        item: Item,
        position: Int
    ) {
        binding.apply {
            ALog.d(TAG, "bindViewdd: ${item.rating}")
            imgView.setImageFromUrl(item.thumbUrl)
            imgView.setOnClickListener {
                onItemClickListener.onItemClick(item, position)
            }
            textTitle.text = item.name
            textRate.text = if (item.rating.isNaN()) "0.0" else item.rating.toString()
            textTitle.setTextColor(theme.firstActivityTextColor(root.context))
            textRate.setTextColor(theme.firstActivityTextColor(root.context))
            textRate.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                if (item.rating > 0) R.drawable.star_filled_24 else R.drawable.star_outline_18,
                0
            )
            root.setOnClickListener {
                onItemClickListener.onItemClick(item, position)
            }
        }
    }

    override fun setData(data: List<Item>, imgDomain: String) {
        super.setData(data, imgDomain)
        ALog.d(TAG, "setData: $imgDomain - - ${this.imgDomain}")
    }

    override fun setupLayout(binding: CardItemBinding, parent: ViewGroup) {
        val lp = binding.root.layoutParams
        lp.height = (parent.height / 2.5).toInt()
        binding.root.layoutParams = lp
    }
}