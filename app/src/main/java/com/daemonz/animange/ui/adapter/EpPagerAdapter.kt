package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.ItemEpPagerBinding
import com.daemonz.animange.entity.EpisodeDetail
import com.daemonz.animange.ui.thememanager.AnimanTheme

class EpPagerAdapter(
    onItemClickListener: OnItemClickListener<List<EpisodeDetail>>,
    private val theme: AnimanTheme,
    private val onEpSelectedListener: (EpisodeDetail) -> Unit
) : BaseRecyclerAdapter<List<EpisodeDetail>, ItemEpPagerBinding>(onItemClickListener, theme) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ItemEpPagerBinding
        get() = ItemEpPagerBinding::inflate

    private var epPerPage = 30
    private var listChildAdapter = mutableListOf<EpisodeListAdapter>()

    override fun bindView(binding: ItemEpPagerBinding, item: List<EpisodeDetail>, position: Int) {
        binding.apply {
            val episodeAdapter = EpisodeListAdapter(
                onItemClickListener = { item, position ->
                    onEpSelectedListener.invoke(item)
                },
                theme = theme
            )
            recyclerEpisodes.adapter = episodeAdapter
            episodeAdapter.setData(item)
            listChildAdapter.add(episodeAdapter)
        }
    }

    fun setEpPerPage(epPerPage: Int) {
        this.epPerPage = epPerPage
    }

    fun setDataEp(data: List<EpisodeDetail>) {
        val pageList = data.chunked(epPerPage)
        listChildAdapter.clear()
        setData(pageList)
    }

    fun setPivot(slug: String) {
        listChildAdapter.forEach {
            it.setPivot(slug)
        }
    }
}