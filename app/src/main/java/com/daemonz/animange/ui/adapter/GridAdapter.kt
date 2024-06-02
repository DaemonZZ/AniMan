package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.CardItemBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.util.setImageFromUrl

class GridAdapter(private val onItemClickListener: OnItemClickListener<Item>) :
    BaseRecyclerAdapter<Item, CardItemBinding>(onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CardItemBinding
        get() = CardItemBinding::inflate

    override fun bindView(binding: CardItemBinding, item: Item, position: Int) {
        binding.apply {
            imgView.setImageFromUrl(item.getImageUrl(imgDomain))
            textTitle.text = item.name
            textSubtitle.text = item.category.map { it.name }.joinToString(
                binding.root.context.getString(
                    R.string.bullet
                )
            )
        }
    }

}