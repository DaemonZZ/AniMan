package com.daemonz.animange.fragment

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentChooseUserBinding
import com.daemonz.animange.entity.User
import com.daemonz.animange.entity.UserType
import com.daemonz.animange.ui.adapter.ChooseUserAdapter
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.viewmodel.HomeViewModel
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class ChooseUserFragment :
    BaseFragment<FragmentChooseUserBinding, HomeViewModel>(FragmentChooseUserBinding::inflate) {
    override val viewModel: HomeViewModel by viewModels<HomeViewModel>()
    private var adapter: ChooseUserAdapter? = null

    override fun setupViews() {
        adapter = ChooseUserAdapter(
            onItemClickListener = { item, index ->
                if (item.userType == UserType.ADD) {
                    if ((LoginData.account?.users?.size ?: 0) in 1..4) {
                        findNavController().navigate(ChooseUserFragmentDirections.actionChooseUserFragmentToUserInfoFragment())
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.user_limit_reached),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@ChooseUserAdapter
                }
                if (adapter?.isEditModeEnabled() == true) {
                    findNavController().navigate(
                        ChooseUserFragmentDirections.actionChooseUserFragmentToUserInfoFragment(
                            item
                        )
                    )
                } else {
                    // apply selected user
                }
            },
        )
        binding.userRecycler.layoutManager = FlexboxLayoutManager(requireContext()).apply {
            justifyContent = JustifyContent.CENTER
        }
        LoginData.account?.let {
            binding.userRecycler.adapter = adapter
            val users = it.users.toMutableList()
            if (users.isNotEmpty() && it.users.size < 5) {
                users.add(User(userType = UserType.ADD))
            }
            adapter?.setData(users)
        }
    }

    override fun setupObservers() {

    }
    fun onEditEnable(enable: Boolean) {
        adapter?.toggleEditMode(enable)
    }
}