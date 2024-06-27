package com.daemonz.animange.fragment.player

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentSuggestionBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.SuggestionAdapter
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.viewmodel.HomeViewModel
import com.daemonz.animange.viewmodel.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuggestionFragment :
    BaseFragment<FragmentSuggestionBinding, HomeViewModel>(FragmentSuggestionBinding::inflate),
    ChildPlayerFragmentActions {
    override val viewModel: HomeViewModel by viewModels()
    private var playerViewModel: PlayerViewModel? = null
    private var suggestionAdapter: SuggestionAdapter? = null

    override fun setupViews() {
        binding.apply {
            suggestionAdapter = SuggestionAdapter(onFavourite = { item ->
                LoginData.getActiveUser()?.let {
                    ALog.d(TAG, "onFavourite: ${it.isFavourite(item.slug)}")
                    if (it.isFavourite(item.slug)) {
                        playerViewModel?.unMarkItemAsFavorite(item)
                    } else {
                        playerViewModel?.markItemAsFavorite(item)
                    }
                }
            },
                onItemClickListener = { item, _ ->
                    ALog.d(TAG, "onItemClick: ${item.slug}")
                    playerViewModel?.loadData(item.slug)
                })
            recyclerSuggest.adapter = suggestionAdapter
        }
    }

    override fun setupObservers() {
        playerViewModel?.suggestions?.observe(viewLifecycleOwner) {
            ALog.d(TAG, "suggestions: ${it.data.items.size}")
            suggestionAdapter?.setData(it.data.items, it.data.imgDomain)
            hideLoadingOverlay("")
        }
    }

    override fun setupViewModel(viewModel: PlayerViewModel) {
        playerViewModel = viewModel
    }
}