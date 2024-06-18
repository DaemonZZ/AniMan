package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
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
        adapter = ChooseUserAdapter { item, index -> TODO("Not yet implemented") }
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
}