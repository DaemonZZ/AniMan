package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.ItemLanguageBinding
import com.daemonz.animange.entity.Language

class LanguageAdapter(onItemClickListener: OnItemClickListener<Language>) :
    BaseRecyclerAdapter<Language, ItemLanguageBinding>(onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ItemLanguageBinding
        get() = ItemLanguageBinding::inflate

    override fun bindView(binding: ItemLanguageBinding, item: Language, position: Int) {
        binding.apply {
            title.text = item.name
        }
    }
}