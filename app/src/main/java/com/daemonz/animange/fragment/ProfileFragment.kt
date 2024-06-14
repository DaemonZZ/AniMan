package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentEditProfileBinding
import com.daemonz.animange.entity.Account
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.viewmodel.HomeViewModel

class ProfileFragment :
    BaseFragment<FragmentEditProfileBinding, HomeViewModel>(FragmentEditProfileBinding::inflate) {
    override val viewModel: HomeViewModel by viewModels()
    private val arg: ProfileFragmentArgs by navArgs()

    override fun setupViews() {
        if (arg.userId == LoginData.account?.id && LoginData.account != null) {
            loadView(LoginData.account!!)
        }
    }

    private fun loadView(acc: Account) {
        binding.apply {
            imgUser.setImageResource(acc.users.first { it.isActive }.getImgResource())
            textUser.text = acc.name
        }
    }

    override fun setupObservers() {

    }
}