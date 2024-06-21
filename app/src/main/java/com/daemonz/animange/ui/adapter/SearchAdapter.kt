package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.SuggestionVideoItemBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.util.setImageFromUrl

class SearchAdapter(
    private val onItemClickListener: OnItemClickListener<Item>,
    private val onFavourite: (Item) -> Unit,
) :
    BaseRecyclerAdapter<Item, SuggestionVideoItemBinding>(onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> SuggestionVideoItemBinding
        get() = SuggestionVideoItemBinding::inflate

    override fun bindView(binding: SuggestionVideoItemBinding, item: Item, position: Int) {
        binding.apply {
            image.setImageFromUrl(
                item.getImageUrl(imgDomain),
                cornerRadius = 100
            )
            textTitle.text = item.name
            textDesc.text = item.originName
            textStatus.text = item.year
            root.setOnClickListener {
                onItemClickListener.onItemClick(item, position)
            }
            favourite.isVisible = false
        }
    }
}