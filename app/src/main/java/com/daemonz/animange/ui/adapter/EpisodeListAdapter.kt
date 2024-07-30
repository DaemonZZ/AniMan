package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.EpisodeItemBinding
import com.daemonz.animange.entity.EpisodeDetail
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.thememanager.AnimanTheme

class EpisodeListAdapter(
    private val onItemClickListener: OnItemClickListener<EpisodeDetail>,
    private val theme: AnimanTheme
) : BaseRecyclerAdapter<EpisodeDetail, EpisodeItemBinding>(onItemClickListener) {
    private var pivot = "1"
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> EpisodeItemBinding
        get() = EpisodeItemBinding::inflate
    private var blinkItem = -1

    override fun bindView(binding: EpisodeItemBinding, item: EpisodeDetail, position: Int) {
        binding.epChip.apply {
            text = item.name
            setOnClickListener {
                onItemClickListener.onItemClick(item, position)
            }
            if (blinkItem == position) {
                ALog.d("Animan", "Blink item: $blinkItem")
                val ani =
                    AnimationUtils.loadAnimation(rootView.context.applicationContext, R.anim.blink)
                postDelayed({ startAnimation(ani) }, 600)
            }
            if (item.slug == pivot) {
                setBackgroundResource(theme.chipBgSelected())
                setTextColor(theme.firstActivityBackgroundColor(this.context))
            } else {
                setBackgroundResource(theme.chipBg())
                setTextColor(theme.firstActivityIconColor(this.context))
            }
        }
    }

    fun setPivot(pivot: String) {
        val oldPivot = data.indexOfFirst { it.slug == this.pivot }
        val newPivot = data.indexOfFirst { it.slug == pivot }
        this.pivot = pivot
        if (oldPivot >= 0) notifyItemChanged(oldPivot)
        if (newPivot >= 0) notifyItemChanged(newPivot)
    }

    fun searchItem(ep: String): Boolean {
        data.forEachIndexed { index, episodeDetail ->
            if (episodeDetail.name == ep) {
                blinkItem = index
                notifyItemChanged(index)
                return true
            }
        }
        return false
    }
}