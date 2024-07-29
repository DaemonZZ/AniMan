package com.daemonz.animange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.daemonz.animange.base.BaseRecyclerAdapter
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.ItemEpPagerBinding
import com.daemonz.animange.entity.EpisodeDetail
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class EpPagerAdapter(
    onItemClickListener: OnItemClickListener<List<EpisodeDetail>>,
    private val theme: AnimanTheme,
    private val onEpSelectedListener: (EpisodeDetail) -> Unit
) : BaseRecyclerAdapter<List<EpisodeDetail>, ItemEpPagerBinding>(onItemClickListener, theme) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ItemEpPagerBinding
        get() = ItemEpPagerBinding::inflate

    var epPerPage = 30
    private var listChildAdapter = mutableMapOf<Int, EpisodeListAdapter>()

    override fun bindView(binding: ItemEpPagerBinding, item: List<EpisodeDetail>, position: Int) {
        binding.apply {
            val episodeAdapter = EpisodeListAdapter(
                onItemClickListener = { item, position ->
                    onEpSelectedListener.invoke(item)
                },
                theme = theme
            )
            recyclerEpisodes.adapter = episodeAdapter
            recyclerEpisodes.layoutManager = FlexboxLayoutManager(
                root.context, FlexDirection.ROW,
                FlexWrap.WRAP
            ).apply {
                alignItems = AlignItems.CENTER
                justifyContent = JustifyContent.SPACE_BETWEEN
            }
            episodeAdapter.setData(item)
            listChildAdapter[position] = episodeAdapter
        }
    }


    fun setDataEp(data: List<EpisodeDetail>) {
        val pageList = data.chunked(epPerPage)
        listChildAdapter.clear()
        setData(pageList)
    }

    fun setPivot(slug: String) {
        listChildAdapter.values.forEach {
            it.setPivot(slug)
        }
    }

    fun markItemFindOut(name: String, onFindOut: (Int) -> Unit) {
        listChildAdapter.forEach { (index, episodeListAdapter) ->
            if (episodeListAdapter.searchItem(name)) {
                onFindOut(index)
            }
        }
    }
}