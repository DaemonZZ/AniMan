package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.daemonz.animange.base.BasePagingAdapter
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.SuggestionVideoItemBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.util.setImageFromUrl

class SearchAdapter(
    private val onItemClickListener: OnItemClickListener<PagingData<Item>>,
) : BasePagingAdapter<Item, PagingData<Item>, SuggestionVideoItemBinding>(onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> SuggestionVideoItemBinding
        get() = SuggestionVideoItemBinding::inflate

    override fun bindView(
        binding: SuggestionVideoItemBinding,
        item: PagingData<Item>,
        position: Int
    ) {
        binding.apply {
            image.setImageFromUrl(
                item.data.getImageUrl(imgDomain),
                cornerRadius = 100
            )
            textTitle.text = item.data.name
            textDesc.text = item.data.originName
            textStatus.text = item.data.year
            root.setOnClickListener {
                onItemClickListener.onItemClick(item, position)
            }
            favourite.isVisible = false
        }
    }
}