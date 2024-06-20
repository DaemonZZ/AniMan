package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.SuggestionVideoItemBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.setImageFromUrl

class SuggestionAdapter(
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
            if (LoginData.getActiveUser()?.isFavourite(item.slug) == true) {
                favourite.setImageResource(R.drawable.favorite_filled)
            } else {
                favourite.setImageResource(R.drawable.favorite)
            }
            favourite.setOnClickListener {
                LoginData.getActiveUser()?.let {
                    onFavourite.invoke(item)
                    notifyItemChanged(position)
                } ?: kotlin.run {
                    Toast.makeText(root.context, R.string.please_login_first, Toast.LENGTH_SHORT)
                        .show()
                }

            }
        }
    }
}