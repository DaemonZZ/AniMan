package com.daemonz.animange.fragment

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentEditProfileBinding
import com.daemonz.animange.entity.Account
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<FragmentEditProfileBinding, ProfileViewModel>(FragmentEditProfileBinding::inflate) {
    override val viewModel: ProfileViewModel by viewModels()
    private val arg: ProfileFragmentArgs by navArgs()

    override fun setupViews() {
        if (arg.userId == LoginData.account?.id && LoginData.account != null) {
            loadView(LoginData.account!!)
        }
    }

    private fun loadView(acc: Account) {
        binding.apply {
            imgUser.setImageResource(acc.users.first { it.isActive }.getImgResource())
            edtName.setText(acc.name)
            edtEmail.setText(acc.email)
            edtPhone.setText(acc.phone)
            save.setOnClickListener {
                viewModel.updateProfile(
                    name = edtName.text.toString(),
                    email = edtEmail.text.toString(),
                    phone = edtPhone.text.toString()
                )
                Toast.makeText(
                    requireContext(),
                    getString(R.string.update_complete),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun setupObservers() {

    }
}