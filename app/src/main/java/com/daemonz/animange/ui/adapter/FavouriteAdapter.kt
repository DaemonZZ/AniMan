package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.CardItemBinding
import com.daemonz.animange.entity.FavouriteItem
import com.daemonz.animange.entity.Item
import com.daemonz.animange.util.setImageFromUrl

class FavouriteAdapter(private val onItemClickListener: OnItemClickListener<FavouriteItem>) :
    BaseRecyclerAdapter<FavouriteItem, CardItemBinding>(onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CardItemBinding
        get() = CardItemBinding::inflate

    override fun bindView(binding: CardItemBinding, item: FavouriteItem, position: Int) {
        binding.apply {
            imgView.setImageFromUrl(item.imageUrl)
            textTitle.text = item.name
            textSubtitle.text = item.category.joinToString(
                binding.root.context.getString(
                    R.string.bullet
                )
            )
        }
    }

}