package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.CardItemBinding
import com.daemonz.animange.databinding.FavoritesItemBinding
import com.daemonz.animange.entity.FavouriteItem
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.setImageFromUrl

class FavouriteAdapter(
    private val onItemClickListener: OnItemClickListener<FavouriteItem>,
    private val onFavourite: (FavouriteItem) -> Unit,
    private var theme: AnimanTheme
) :
    BaseRecyclerAdapter<FavouriteItem, CardItemBinding>(onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CardItemBinding
        get() = CardItemBinding::inflate

    override fun bindView(binding: CardItemBinding, item: FavouriteItem, position: Int) {
        binding.apply {
            imgView.setImageFromUrl(item.imageUrl)
            textTitle.text = item.name
            bookMark.visibility = View.VISIBLE
            root.setOnClickListener {
                onItemClickListener.onItemClick(item, position)
            }
            if (LoginData.getActiveUser()?.isFavourite(item.slug) == true) {
                bookMark.setImageResource(theme.bookmarkFilledIcon())
            } else {
                bookMark.setImageResource(theme.bookmarkIcon())
            }
            textRate.text = if (item.rating.isNaN()) "0.0" else item.rating.toString()
            textRate.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                if (item.rating > 0) R.drawable.star_filled_24 else R.drawable.star_outline_18,
                0
            )
            bookMark.setOnClickListener {
                LoginData.getActiveUser()?.let {
                    onFavourite.invoke(item)
                    notifyItemChanged(position)
                } ?: kotlin.run {
                    Toast.makeText(root.context, R.string.please_login_first, Toast.LENGTH_SHORT)
                        .show()
                }

            }
            textTitle.setTextColor(theme.firstActivityTextColor(root.context))
            textRate.setTextColor(theme.firstActivityTextColor(root.context))
        }
    }

    override fun setupLayout(binding: CardItemBinding, parent: ViewGroup) {
        //set item height according to screen size
        val lp = binding.root.layoutParams
        lp.height = parent.height / 3
        binding.root.layoutParams = lp
    }

    override fun syncTheme(theme: AnimanTheme) {
        this.theme = theme
        notifyDataSetChanged()
    }
}