package com.daemonz.animange.fragment.player

import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentEpisodeBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.EpPagerAdapter
import com.daemonz.animange.ui.view_helper.CirclePagerIndicatorDecoration
import com.daemonz.animange.util.AppUtils
import com.daemonz.animange.viewmodel.HomeViewModel
import com.daemonz.animange.viewmodel.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EpisodesFragment :
    BaseFragment<FragmentEpisodeBinding, HomeViewModel>(FragmentEpisodeBinding::inflate),
    ChildPlayerFragmentActions {
    override val viewModel: HomeViewModel by viewModels()
    private var playerViewModel: PlayerViewModel? = null
    private var pagerAdapter: EpPagerAdapter? = null

    override fun setupViews() {
        binding.apply {
            pagerAdapter = EpPagerAdapter(
                onItemClickListener = { _, _ -> },
                theme = currentTheme,
                onEpSelectedListener = { ep ->
                    pagerAdapter?.setPivot(ep.slug)
                    playerViewModel?.chooseEpisode(ep.slug)
                }
            )
            recyclerPager.adapter = pagerAdapter
            recyclerPager.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            recyclerPager.addItemDecoration(
                CirclePagerIndicatorDecoration(
                    colorInactive = currentTheme.indicatorInactive(requireContext()),
                    colorActive = currentTheme.indicatorActive(requireContext())
                )
            )
            val snapHelper = LinearSnapHelper()
            recyclerPager.onFlingListener = null
            snapHelper.attachToRecyclerView(recyclerPager)

            edtSearch.setOnEditorActionListener { v, _, _ ->
                val text = v.text.toString()
                ALog.d(TAG, "onEditorAction: , $text")
                AppUtils.hideKeyboard(requireContext(), v)
                if (text.isNotEmpty() && pagerAdapter != null) {
                    pagerAdapter!!.markItemFindOut(text, onFindOut = { index ->
                        ALog.d(TAG, "find episode $text at position $index")
                        recyclerPager.smoothScrollToPosition(index)
                    })
                    return@setOnEditorActionListener true
                }
                false
            }
        }
    }

    override fun setupObservers() {
        playerViewModel?.apply {
            currentPlaying.observe(viewLifecycleOwner) {
//                episodeAdapter?.setPivot(it.pivot)
//                binding.recyclerEpisodes.smoothScrollToPosition(it.pivot)
            }
            playerData.observe(viewLifecycleOwner) { data ->
                data.data.item?.let {
                    pagerAdapter?.let { pager ->
                        pager.setDataEp(it.episodes.first().serverData)
                        binding.recyclerPager.smoothScrollToPosition(pager.itemCount - 1)
                    }
                }
            }
        }
    }

    override fun setupViewModel(viewModel: PlayerViewModel) {
        playerViewModel = viewModel
    }

    override fun syncTheme() {
        super.syncTheme()
        pagerAdapter?.setTheme(currentTheme)
        binding.apply {
            edtSearch.setHintTextColor(currentTheme.firstActivityTextColor(requireContext()))
            edtSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(
                ContextCompat.getDrawable(root.context, currentTheme.iconSearch()), null, null, null
            )
        }
    }
}