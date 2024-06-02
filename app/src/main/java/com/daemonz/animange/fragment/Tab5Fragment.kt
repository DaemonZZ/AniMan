package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentTab1Binding
import com.daemonz.animange.databinding.FragmentTab5Binding
import com.daemonz.animange.ui.CommonAction
import com.daemonz.animange.viewmodel.HomeViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database

class Tab5Fragment : BaseFragment<FragmentTab5Binding, HomeViewModel>(FragmentTab5Binding::inflate),
    CommonAction {
    override val viewModel: HomeViewModel by viewModels()

    override fun setupViews() {
        binding.apply {
            btn.setOnClickListener {
                val firebase = Firebase.database.reference
            }
        }
    }

    override fun setupObservers() {
    }

    override fun onRefresh() {
        TODO("Not yet implemented")
    }

    override fun onReSelectBottomNavigationItem(itemId: Int) {
        TODO("Not yet implemented")
    }
}