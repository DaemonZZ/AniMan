package com.daemonz.animange.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.ItemMangaPageBinding
import com.daemonz.animange.entity.manga.ImagePage
import com.daemonz.animange.util.setImageFromUrl

class MangaPageAdapter(onItemClickListener: OnItemClickListener<ImagePage>) :
    BaseRecyclerAdapter<ImagePage, ItemMangaPageBinding>(onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ItemMangaPageBinding
        get() = ItemMangaPageBinding::inflate

    override fun bindView(
        binding: ItemMangaPageBinding,
        item: ImagePage,
        position: Int
    ) {
        binding.apply {
            mangaPage.setImageFromUrl(item.url)
        }
    }
}