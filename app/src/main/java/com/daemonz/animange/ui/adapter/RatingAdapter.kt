package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.ItemRatingBinding
import com.daemonz.animange.entity.FilmRating
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.daemonz.animange.util.loadImageFromStorage

class RatingAdapter(
    onclick: OnItemClickListener<FilmRating>,
    private val theme: AnimanTheme
) : BaseRecyclerAdapter<FilmRating, ItemRatingBinding>(onclick) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ItemRatingBinding
        get() = ItemRatingBinding::inflate
    private var listStar: List<AppCompatImageView> = listOf()

    override fun bindView(binding: ItemRatingBinding, item: FilmRating, position: Int) {
        binding.apply {
            listStar = listOf(
                start1, start2, start3, start4, start5
            )
            textName.text = item.user?.name
            imgAvt.loadImageFromStorage(item.user?.image ?: 1)
            textContent.text = item.comment
            listStar.forEachIndexed { i, star ->
                if (i <= item.rating - 1) {
                    star.setImageResource(R.drawable.star_filled)
                } else {
                    star.setImageResource(R.drawable.star_outline)
                }
            }

        }
    }
}