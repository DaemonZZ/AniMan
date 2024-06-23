package com.daemonz.animange.fragment

import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daemonz.animange.MainActivity
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentUserInfoBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.loadImageFromStorage
import com.daemonz.animange.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserInfoFragment :
    BaseFragment<FragmentUserInfoBinding, ProfileViewModel>(FragmentUserInfoBinding::inflate) {
    override val viewModel: ProfileViewModel by hiltNavGraphViewModels(R.id.nav_profile)
    private val args: UserInfoFragmentArgs by navArgs()

    override fun setupViews() {
        binding.apply {
            imgUser.loadImageFromStorage(args.user?.image ?: 1)
            edtName.setText(args.user?.name)
            imgUser.setOnClickListener {
                findNavController().navigate(UserInfoFragmentDirections.actionUserInfoFragmentToChooseAvatarFragment())
            }
            save.setOnClickListener {
                val name =
                    if (edtName.text.isNullOrEmpty()) args.user?.name else edtName.text.toString()
                args.user?.id?.let { id ->
                    viewModel.updateUser(name, viewModel.currentAvt.value, id)
                } ?: run {
                    viewModel.newUser(name, viewModel.currentAvt.value ?: 1)
                }
                findNavController().popBackStack()
            }
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
        viewModel.currentAvt.observe(viewLifecycleOwner) {
            binding.imgUser.loadImageFromStorage(it)
        }
    }
}