package com.daemonz.animange.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PlayerPagerAdapter(private val fragments: List<Fragment>, parent: Fragment) :
    FragmentStateAdapter(parent) {

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}