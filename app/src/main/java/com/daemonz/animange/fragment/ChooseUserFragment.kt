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
import com.daemonz.animange.viewmodel.ProfileViewModel
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseUserFragment :
    BaseFragment<FragmentChooseUserBinding, ProfileViewModel>(FragmentChooseUserBinding::inflate) {
    override val viewModel: ProfileViewModel by viewModels<ProfileViewModel>()
    private var adapter: ChooseUserAdapter? = null

    override fun setupViews() {
        adapter = ChooseUserAdapter(
            onItemClickListener = { item, _ ->
                if (item.userType == UserType.ADD) {
                    if ((LoginData.account?.users?.size ?: 0) in 1..4) {
                        findNavController().navigate(ChooseUserFragmentDirections.toProfile())
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
                        ChooseUserFragmentDirections.toProfile(
                            item
                        )
                    )
                } else {
                    if (!item.isActive) {
                        item.id?.let {
                            viewModel.switchUser(it)
                            loadDataUser()
                        }
                    }
                }
            },
            theme = currentTheme
        )
        binding.userRecycler.layoutManager = FlexboxLayoutManager(requireContext()).apply {
            justifyContent = JustifyContent.CENTER
        }
        loadDataUser()
    }

    private fun loadDataUser() {
        LoginData.account?.let {
            binding.userRecycler.adapter = adapter
            val users = it.users.toMutableList()
            if (users.isNotEmpty() && it.users.size < 5) {
                users.add(User(userType = UserType.ADD))
            }
            adapter?.setData(users)
        }
    }

    override fun setupObservers() {}
    fun onEditEnable(enable: Boolean) {
        adapter?.toggleEditMode(enable)
    }
}