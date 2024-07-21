package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.EpisodeItemBinding
import com.daemonz.animange.entity.Episode
import com.daemonz.animange.entity.EpisodeDetail
import com.daemonz.animange.ui.thememanager.AnimanTheme

class EpisodeListAdapter(
    private val onItemClickListener: OnItemClickListener<EpisodeDetail>,
    private val theme: AnimanTheme
) : BaseRecyclerAdapter<EpisodeDetail, EpisodeItemBinding>(onItemClickListener, theme) {
    private var pivot = 0
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> EpisodeItemBinding
        get() = EpisodeItemBinding::inflate

    override fun bindView(binding: EpisodeItemBinding, item: EpisodeDetail, position: Int) {
        binding.epChip.apply {
            text = context.getString(R.string.episode_num, (position + 1).toString())
            setOnClickListener {
                onItemClickListener.onItemClick(item, position)
            }
            isChecked = position == pivot
        }
    }

    fun setDataEpisode(data: Episode) {
        this.pivot = data.pivot
        setData(data.serverData)
    }

    fun setPivot(pivot: Int) {
        val oldPivot = this.pivot
        this.pivot = pivot
        notifyItemChanged(pivot)
        notifyItemChanged(oldPivot)
    }
}