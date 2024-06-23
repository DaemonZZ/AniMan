package com.daemonz.animange.fragment

import android.os.SystemClock
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentEditProfileBinding
import com.daemonz.animange.entity.Account
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.loadImageFromStorage
import com.daemonz.animange.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<FragmentEditProfileBinding, ProfileViewModel>(FragmentEditProfileBinding::inflate) {
    override val viewModel: ProfileViewModel by viewModels()
    private val arg: ProfileFragmentArgs by navArgs()

    private var countClick = 0
    private var lastClickTime = 0L

    override fun setupViews() {
        if (arg.userId == LoginData.account?.id && LoginData.account != null) {
            loadView(LoginData.account!!)
        }
    }

    private fun loadView(acc: Account) {
        binding.apply {
            val user = acc.users.first { it.isActive }
            ALog.d(TAG, "loadView  ${user.image}")
            imgUser.loadImageFromStorage(acc.users.first { it.isActive }.image ?: 1)
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
            imgUser.setOnClickListener {
                val time = SystemClock.elapsedRealtime()
                if (time - lastClickTime < 1000) {
                    countClick++
                } else {
                    countClick = 0
                }
                lastClickTime = time
                if (countClick == 5) {
                    countClick = 0
                    findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToSecretFragment())
                }
            }
        }
    }

    override fun setupObservers() {

    }
}