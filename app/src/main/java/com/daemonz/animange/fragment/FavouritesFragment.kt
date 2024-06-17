package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentFavouritesBinding
import com.daemonz.animange.viewmodel.HomeViewModel

class FavouritesFragment :
    BaseFragment<FragmentFavouritesBinding, HomeViewModel>(FragmentFavouritesBinding::inflate) {
    override val viewModel: HomeViewModel by viewModels()

    override fun setupViews() {

    }

    override fun setupObservers() {

    }
}