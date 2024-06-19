package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.daemonz.animange.MainActivity
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentUserInfoBinding
import com.daemonz.animange.viewmodel.HomeViewModel

class UserInfoFragment :
    BaseFragment<FragmentUserInfoBinding, HomeViewModel>(FragmentUserInfoBinding::inflate) {
    override val viewModel: HomeViewModel by viewModels<HomeViewModel>()
    private val args: UserInfoFragmentArgs by navArgs()

    override fun setupViews() {
        binding.apply {
            imgUser.setImageResource(args.user?.getImgResource() ?: R.drawable.avt_1)
            edtName.setText(args.user?.name)
        }
    }

    override fun onResume() {
        super.onResume()
        if (args.user != null) {
            (activity as? MainActivity)?.setTitle(getString(R.string.edit_user))
        } else {
            (activity as? MainActivity)?.setTitle(getString(R.string.new_user))
        }
    }

    override fun setupObservers() {

    }
}