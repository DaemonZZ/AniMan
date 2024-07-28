package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.daemonz.animange.R
import com.daemonz.animange.base.BasePagingAdapter
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.CardItemBinding
import com.daemonz.animange.databinding.SuggestionVideoItemBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.setImageFromUrl

class SuggestionAdapter(
    private val onItemClickListener: OnItemClickListener<PagingData<Item>>,
    private val theme: AnimanTheme
) : BasePagingAdapter<Item, PagingData<Item>, CardItemBinding>(onItemClickListener, theme) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CardItemBinding
        get() = CardItemBinding::inflate


    override fun bindView(binding: CardItemBinding, item: PagingData<Item>, position: Int) {
        binding.apply {
            imgView.setImageFromUrl(item.data.getImageUrl(imgDomain))
            textTitle.text = item.data.name
            textSubtitle.text = item.data.category.joinToString(
                binding.root.context.getString(
                    R.string.bullet
                )
            ) { it.name }

            root.setCardBackgroundColor(theme.menuItemBackground(root.context))
        }
    }

    override fun setupLayout(binding: CardItemBinding, parent: ViewGroup) {
        //set item height according to screen size
        val lp = binding.root.layoutParams
        lp.width = parent.width / 2
        binding.root.layoutParams = lp
    }
}