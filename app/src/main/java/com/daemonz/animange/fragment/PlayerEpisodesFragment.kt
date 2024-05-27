package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentEpisodesBinding
import com.daemonz.animange.viewmodel.MainViewModel
import com.daemonz.animange.viewmodel.PlayerViewModel

class PlayerEpisodesFragment :
    BaseFragment<FragmentEpisodesBinding, MainViewModel>(FragmentEpisodesBinding::inflate) {
    override val viewModel: MainViewModel by viewModels()
    private var playerViewModel: PlayerViewModel? = null

    override fun setupViews() {
    }

    override fun setupObservers() {
    }

    fun setViewModel(viewModel: PlayerViewModel) {
        this.playerViewModel = viewModel
    }
}