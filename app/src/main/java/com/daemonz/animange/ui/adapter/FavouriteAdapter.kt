package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FavoritesItemBinding
import com.daemonz.animange.entity.FavouriteItem
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.setImageFromUrl

class FavouriteAdapter(
    private val onItemClickListener: OnItemClickListener<FavouriteItem>,
    private val onFavourite: (FavouriteItem) -> Unit,
    private val theme: AnimanTheme
) :
    BaseRecyclerAdapter<FavouriteItem, FavoritesItemBinding>(onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FavoritesItemBinding
        get() = FavoritesItemBinding::inflate

    override fun bindView(binding: FavoritesItemBinding, item: FavouriteItem, position: Int) {
        binding.apply {
            image.setImageFromUrl(item.imageUrl)
            textTitle.text = item.name
            textStatus.text = item.episodeCurrent
            root.setOnClickListener {
                onItemClickListener.onItemClick(item, position)
            }
            if (LoginData.getActiveUser()?.isFavourite(item.slug) == true) {
                favourite.setImageResource(R.drawable.bookmark_filled_night)
            } else {
                favourite.setImageResource(R.drawable.bookmark_night)
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

    override fun setupLayout(binding: FavoritesItemBinding, parent: ViewGroup) {
        //set item height according to screen size
        val lp = binding.root.layoutParams
        lp.height = parent.height / 5
        binding.image.layoutParams = lp
    }
}