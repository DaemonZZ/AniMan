package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.CardItemBinding
import com.daemonz.animange.util.toImageResource

class AvatarGridAdapter(private val onItemClickListener: OnItemClickListener<Int>) :
    BaseRecyclerAdapter<Int, CardItemBinding>(onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CardItemBinding
        get() = CardItemBinding::inflate

    override fun bindView(binding: CardItemBinding, item: Int, position: Int) {
        binding.apply {
            imgView.setImageResource(item.toImageResource())
        }
    }

}