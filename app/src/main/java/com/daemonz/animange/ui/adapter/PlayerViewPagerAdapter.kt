package com.daemonz.animange.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.daemonz.animange.fragment.PlayerEpisodesFragment
import com.daemonz.animange.fragment.PlayerOverViewFragment
import com.daemonz.animange.viewmodel.PlayerViewModel

class PlayerViewPagerAdapter(private val viewModel: PlayerViewModel, fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        if (position == 0) {
            return PlayerOverViewFragment().apply {
                setViewModel(viewModel = viewModel)
            }
        } else {
            return PlayerEpisodesFragment().apply {
                setViewModel(viewModel = viewModel)
            }
        }
    }
}