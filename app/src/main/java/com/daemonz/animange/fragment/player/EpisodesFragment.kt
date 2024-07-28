package com.daemonz.animange.fragment.player

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearSnapHelper
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentEpisodeBinding
import com.daemonz.animange.ui.adapter.EpPagerAdapter
import com.daemonz.animange.ui.adapter.EpisodeListAdapter
import com.daemonz.animange.ui.view_helper.CirclePagerIndicatorDecoration
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
            recyclerPager.addItemDecoration(
                CirclePagerIndicatorDecoration(
                    colorInactive = currentTheme.indicatorInactive(requireContext()),
                    colorActive = currentTheme.indicatorActive(requireContext())
                )
            )
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(recyclerPager)
        }
    }

    override fun setupObservers() {
        playerViewModel?.apply {
            currentPlaying.observe(viewLifecycleOwner) {
//                episodeAdapter?.setPivot(it.pivot)
//                binding.recyclerEpisodes.smoothScrollToPosition(it.pivot)
            }
            playerData.observe(viewLifecycleOwner) { data ->
                data.data.item?.let { pagerAdapter?.setDataEp(it.episodes.first().serverData) }
            }
        }

    }

    override fun setupViewModel(viewModel: PlayerViewModel) {
        playerViewModel = viewModel
    }
}