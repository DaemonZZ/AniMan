package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentSearchFilterBinding
import com.daemonz.animange.viewmodel.SearchFilterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFilterFragment :
    BaseFragment<FragmentSearchFilterBinding, SearchFilterViewModel>(FragmentSearchFilterBinding::inflate) {
    override val viewModel: SearchFilterViewModel by viewModels()

    override fun setupViews() {

    }

    override fun setupObservers() {

    }
}