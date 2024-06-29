package com.daemonz.animange.fragment.player

import com.daemonz.animange.viewmodel.PlayerViewModel

fun interface ChildPlayerFragmentActions {
    fun setupViewModel(viewModel: PlayerViewModel)
}