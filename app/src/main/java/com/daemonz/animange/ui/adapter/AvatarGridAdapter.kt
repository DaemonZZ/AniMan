package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.AvtCardItemBinding
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.daemonz.animange.util.loadImageFromStorage

class AvatarGridAdapter(
    onItemClickListener: OnItemClickListener<Int>,
    private val theme: AnimanTheme
) :
    BaseRecyclerAdapter<Int, AvtCardItemBinding>(onItemClickListener) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> AvtCardItemBinding
        get() = AvtCardItemBinding::inflate

    override fun bindView(binding: AvtCardItemBinding, item: Int, position: Int) {
        binding.apply {
            imgView.loadImageFromStorage(item)
        }
    }

}