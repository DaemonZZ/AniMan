package com.daemonz.animange.fragment

import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentSettingBinding
import com.daemonz.animange.databinding.FragmentTab5Binding
import com.daemonz.animange.entity.SettingItem
import com.daemonz.animange.entity.SettingItemType
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.BottomNavigationAction
import com.daemonz.animange.ui.adapter.SettingAdapter
import com.daemonz.animange.ui.dialog.SearchDialog
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.setImageFromUrl
import com.daemonz.animange.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Tab5Fragment :
    BaseFragment<FragmentSettingBinding, LoginViewModel>(FragmentSettingBinding::inflate),
    BottomNavigationAction {
    override val viewModel: LoginViewModel by activityViewModels()


    override fun setupViews() {
        loadViewState()
        binding.apply {
            profile.textTitle.text = getString(R.string.profile)
            profile.icon.setImageResource(R.drawable.ic_profile)
            profile.root.setOnClickListener {
                //go to profile screen
            }
            favourite.textTitle.text = getString(R.string.favourite)
            favourite.icon.setImageResource(R.drawable.ic_favourite)
            favourite.root.setOnClickListener {
                //go to favourite screen
            }
            feedback.textTitle.text = getString(R.string.feedback)
            feedback.icon.setImageResource(R.drawable.ic_feedback)
            feedback.root.setOnClickListener {
                //go to feedback screen
            }
            support.textTitle.text = getString(R.string.support)
            support.icon.setImageResource(R.drawable.ic_support)
            support.root.setOnClickListener {
                //go to support screen
            }
        }
    }

    private fun loadViewState() {
        binding.apply {
            if (viewModel.isLoggedIn()) {
                layoutLogin.isVisible = false
                groupAccount.visibility = View.VISIBLE
                LoginData.getActiveUser()?.let {
                    imgUser.setImageFromUrl(it.imageUrl)
                }
            } else {
                layoutLogin.isVisible = true
                groupAccount.visibility = View.INVISIBLE
                layoutLogin.setOnClickListener {
                    viewModel.createSigningLauncher()
                }
            }
        }
    }

    override fun setupObservers() {
        viewModel.apply {
            error.observe(viewLifecycleOwner) {
                ALog.e(TAG, "setupObservers: $it")
                if (it != null) {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
            account.observe(viewLifecycleOwner) {
                loadViewState()
            }
        }
    }

    override fun onSearch() {
        //
    }

    override fun onRefresh() {
        TODO("Not yet implemented")
    }

    override fun onReSelectBottomNavigationItem(itemId: Int) {
        TODO("Not yet implemented")
    }

    override fun initData() {

    }

}