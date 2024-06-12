package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentChooseUserBinding
import com.daemonz.animange.entity.User
import com.daemonz.animange.entity.UserType
import com.daemonz.animange.ui.adapter.ChooseUserAdapter
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.makeTextLink
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
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.textEdit.makeTextLink(
            requireContext().getString(R.string.edit),
            underline = true,
            color = null,
            onClick = {
                //
            }
        )
    }

    override fun setupObservers() {

    }
}