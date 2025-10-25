package com.daemonz.animange.fragment.manga

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentSuggestionBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.SuggestionAdapter
import com.daemonz.animange.viewmodel.manga.ReadMangaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuggestionMangaFragment :
    BaseFragment<FragmentSuggestionBinding, ReadMangaViewModel>(FragmentSuggestionBinding::inflate) {
    override val viewModel: ReadMangaViewModel by activityViewModels()
    private var suggestionAdapter: SuggestionAdapter? = null

    override fun setupViews() {
        binding.apply {
            suggestionAdapter = SuggestionAdapter(
                onItemClickListener = { item, _ ->
//                    ALog.d(TAG, "onItemClick: ${item.data.slug}")
//                    viewModel?.loadData(item.data.slug)
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
                            viewModel.getSuggestions(page = page + 1)
                        }
                    }
                }
            })
            viewModel.suggestion.value?.let {
                ALog.d(TAG, "suggestions: ${it.size}")
                suggestionAdapter?.setData(it, viewModel.imgDomain)
                hideLoadingOverlay()
            }
        }
    }

    override fun setupObservers() {
//        viewModel.suggestions.observe(viewLifecycleOwner) {
//            ALog.d(TAG, "suggestions: ${it.size}")
//            suggestionAdapter?.setData(it, viewModel.imgDomain)
//            hideLoadingOverlay()
//        }
//        playerViewModel?.currentPlaying?.observe(viewLifecycleOwner) {
//            playerViewModel?.playerData?.value?.data?.item?.category?.random()?.let {
//                viewModel.getSuggestions(it, 0)
//                showLoadingOverlay()
//            }
//
//        }
    }

    override fun syncTheme() {
        super.syncTheme()
        setupViews()
        viewModel.currentManga?.let {
//            suggestionAdapter?.setData(it, viewModel.imgDomain)
            hideLoadingOverlay()
        }
    }
}