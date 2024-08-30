package com.daemonz.animange.fragment.player

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentSuggestionBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.SuggestionAdapter
import com.daemonz.animange.viewmodel.PlayerViewModel
import com.daemonz.animange.viewmodel.SuggestionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuggestionFragment :
    BaseFragment<FragmentSuggestionBinding, SuggestionViewModel>(FragmentSuggestionBinding::inflate),
    ChildPlayerFragmentActions {
    override val viewModel: SuggestionViewModel by viewModels()
    private var playerViewModel: PlayerViewModel? = null
    private var suggestionAdapter: SuggestionAdapter? = null

    override fun setupViews() {
        binding.apply {
            suggestionAdapter = SuggestionAdapter(
                onItemClickListener = { item, _ ->
                    ALog.d(TAG, "onItemClick: ${item.data.slug}")
                    playerViewModel?.loadData(item.data.slug)
                },
                theme = currentTheme
            )
            recyclerSuggest.adapter = suggestionAdapter
            recyclerSuggest.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            recyclerSuggest.addOnScrollListener(object : OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (!recyclerView.canScrollHorizontally(1)) {
                        suggestionAdapter?.lastPage?.let { page ->
                            playerViewModel?.playerData?.value?.data?.item?.category?.random()
                                ?.let {
                                    viewModel.getSuggestions(it, page + 1)
                                    showLoadingOverlay()
                                }
                        }
                    }
                }
            })
            viewModel.suggestions.value?.let {
                ALog.d(TAG, "suggestions: ${it.size}")
                suggestionAdapter?.setData(it, viewModel.imgDomain)
                hideLoadingOverlay()
            }
        }
    }

    override fun setupObservers() {
        viewModel.suggestions.observe(viewLifecycleOwner) {
            ALog.d(TAG, "suggestions: ${it.size}")
            suggestionAdapter?.setData(it, viewModel.imgDomain)
            hideLoadingOverlay()
        }
        playerViewModel?.currentPlaying?.observe(viewLifecycleOwner) {
            playerViewModel?.playerData?.value?.data?.item?.category?.random()?.let {
                viewModel.getSuggestions(it, 0)
                showLoadingOverlay()
            }

        }
    }

    override fun setupViewModel(viewModel: PlayerViewModel) {
        playerViewModel = viewModel
    }

    override fun syncTheme() {
        super.syncTheme()
        setupViews()
        viewModel.suggestions.value?.let {
            suggestionAdapter?.setData(it, viewModel.imgDomain)
            hideLoadingOverlay()
        }
    }
}