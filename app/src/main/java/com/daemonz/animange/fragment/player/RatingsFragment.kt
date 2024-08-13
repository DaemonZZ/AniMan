package com.daemonz.animange.fragment.player

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentRatingsBinding
import com.daemonz.animange.ui.adapter.RatingAdapter
import com.daemonz.animange.viewmodel.HomeViewModel
import com.daemonz.animange.viewmodel.PlayerViewModel

class RatingsFragment :
    BaseFragment<FragmentRatingsBinding, HomeViewModel>(FragmentRatingsBinding::inflate),
    ChildPlayerFragmentActions {
    override val viewModel: HomeViewModel by viewModels()
    private var playerViewModel: PlayerViewModel? = null
    private var adapter: RatingAdapter? = null

    override fun setupViews() {
        adapter = RatingAdapter(
            onclick = { _, _ -> },
            theme = currentTheme
        )
        binding.recycler.adapter = adapter
    }

    override fun setupObservers() {
        playerViewModel?.allRatings?.observe(viewLifecycleOwner) {
            binding.recycler.isVisible = it.isNotEmpty()
            binding.textNoComment.isVisible = it.isEmpty()
            adapter?.setData(it)
        }
    }

    override fun setupViewModel(viewModel: PlayerViewModel) {
        playerViewModel = viewModel
    }

    override fun syncTheme() {
        super.syncTheme()
        binding.textNoComment.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
    }
}