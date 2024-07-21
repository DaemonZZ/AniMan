package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.daemonz.animange.R
import com.daemonz.animange.base.BasePagingAdapter
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.SuggestionVideoItemBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.setImageFromUrl

class SuggestionAdapter(
    private val onItemClickListener: OnItemClickListener<PagingData<Item>>,
    private val onFavourite: (Item) -> Unit,
    private val theme: AnimanTheme
) : BasePagingAdapter<Item, PagingData<Item>, SuggestionVideoItemBinding>(
    onItemClickListener,
    theme
) {
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
            root.setCardBackgroundColor(theme.firstActivityBackgroundColor(root.context))
            textTitle.text = item.data.name
            textTitle.setTextColor(theme.firstActivityTextColor(root.context))
            textDesc.text = item.data.originName
            textDesc.setTextColor(theme.firstActivityTextColor(root.context))
            textStatus.text = item.data.year
            textStatus.setTextColor(theme.firstActivityTextColor(root.context))
            root.setOnClickListener {
                onItemClickListener.onItemClick(item, position)
            }
            if (LoginData.getActiveUser()?.isFavourite(item.data.slug) == true) {
                favourite.setImageResource(R.drawable.favorite_filled)
            } else {
                favourite.setImageResource(R.drawable.favorite)
            }
            favourite.setOnClickListener {
                LoginData.getActiveUser()?.let {
                    onFavourite.invoke(item.data)
                    notifyItemChanged(position)
                } ?: kotlin.run {
                    Toast.makeText(root.context, R.string.please_login_first, Toast.LENGTH_SHORT)
                        .show()
                }

            }
        }
    }
}