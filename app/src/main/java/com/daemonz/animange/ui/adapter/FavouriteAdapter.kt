package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.SuggestionVideoItemBinding
import com.daemonz.animange.entity.FavouriteItem
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.setImageFromUrl

class FavouriteAdapter(
    private val onItemClickListener: OnItemClickListener<FavouriteItem>,
    private val onFavourite: (FavouriteItem) -> Unit,
) :
    BaseRecyclerAdapter<FavouriteItem, SuggestionVideoItemBinding>(onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> SuggestionVideoItemBinding
        get() = SuggestionVideoItemBinding::inflate

    override fun bindView(binding: SuggestionVideoItemBinding, item: FavouriteItem, position: Int) {
        binding.apply {
            image.setImageFromUrl(item.imageUrl)
            textTitle.text = item.name
            textDesc.text = item.originName
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