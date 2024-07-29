package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.EpisodeItemBinding
import com.daemonz.animange.entity.Episode
import com.daemonz.animange.entity.EpisodeDetail
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.thememanager.AnimanTheme

class EpisodeListAdapter(
    private val onItemClickListener: OnItemClickListener<EpisodeDetail>,
    private val theme: AnimanTheme
) : BaseRecyclerAdapter<EpisodeDetail, EpisodeItemBinding>(onItemClickListener, theme) {
    private var pivot = ""
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> EpisodeItemBinding
        get() = EpisodeItemBinding::inflate

    override fun bindView(binding: EpisodeItemBinding, item: EpisodeDetail, position: Int) {
        binding.epChip.apply {
            text = item.name
            setOnClickListener {
                onItemClickListener.onItemClick(item, position)
            }
//            isChecked = item.slug == pivot
        }
    }

    fun setDataEpisode(data: Episode) {
        this.pivot = data.pivot
        setData(data.serverData)
    }

    fun setPivot(pivot: String) {
        val oldPivot = data.indexOfFirst { it.slug == this.pivot }
        val newPivot = data.indexOfFirst { it.slug == pivot }
        this.pivot = pivot
        if (oldPivot >= 0) notifyItemChanged(oldPivot)
        if (newPivot >= 0) notifyItemChanged(newPivot)
    }

    fun searchItem(ep: String): Boolean {
        data.forEach {
            if (it.name == ep) {
                return true
            }
        }
        return false
    }
}