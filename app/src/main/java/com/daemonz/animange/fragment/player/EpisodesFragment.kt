package com.daemonz.animange.fragment.player

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentEpisodeBinding
import com.daemonz.animange.ui.adapter.EpisodeListAdapter
import com.daemonz.animange.viewmodel.HomeViewModel
import com.daemonz.animange.viewmodel.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EpisodesFragment :
    BaseFragment<FragmentEpisodeBinding, HomeViewModel>(FragmentEpisodeBinding::inflate),
    ChildPlayerFragmentActions {
    override val viewModel: HomeViewModel by viewModels()

    private var playerViewModel: PlayerViewModel? = null
    private var episodeAdapter: EpisodeListAdapter? = null

    override fun setupViews() {
        binding.apply {
            episodeAdapter = EpisodeListAdapter(
                onItemClickListener = { _, index ->
                    episodeAdapter?.setPivot(index)
                    playerViewModel?.chooseEpisode(index)
                },
                theme = currentTheme
            )
            recyclerEpisodes.adapter = episodeAdapter
        }
    }

    override fun setupObservers() {
        playerViewModel?.apply {
            currentPlaying.observe(viewLifecycleOwner) {
                episodeAdapter?.setPivot(it.pivot)
                binding.recyclerEpisodes.smoothScrollToPosition(it.pivot)
            }
            playerData.observe(viewLifecycleOwner) { data ->
                data.data.item?.let { episodeAdapter?.setDataEpisode(it.episodes.first()) }
                val serverList =
                    data.data.item?.episodes?.map { it.serverName }?.toTypedArray() ?: emptyArray()
                binding.dropdownText.apply {
                    setSimpleItems(serverList)
                    setText(serverList.firstOrNull())
                    setOnItemClickListener { _, _, position, _ ->
                        playerViewModel?.chooseEpisode(
                            playerViewModel?.currentPlaying?.value?.pivot ?: 0, server = position
                        )
                    }
                }
            }
        }

    }

    override fun setupViewModel(viewModel: PlayerViewModel) {
        playerViewModel = viewModel
    }
}