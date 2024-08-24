package com.daemonz.animange.fragment

import android.widget.Toast
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daemonz.animange.MainActivity
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentUserInfoBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.dialog.DeleteDialog
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.isValidName
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
            edtPassword.setText(args.user?.password ?: "")
            imgUser.setOnClickListener {
                findNavController().navigate(UserInfoFragmentDirections.actionUserInfoFragmentToChooseAvatarFragment())
            }
            save.setOnClickListener {
                if (!edtName.text.toString().isValidName()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.invalid_name),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                if (edtPassword.text?.length != 0 && edtPassword.text?.length != 6) {
                    Toast.makeText(
                        requireContext(),
                        edtPassword.text?.length.toString() + getString(R.string.invalid_passord),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                val name =
                    if (edtName.text.isNullOrEmpty()) args.user?.name else edtName.text.toString()
                val password =
                    if (edtPassword.text?.length == 6) edtPassword.text?.toString() else null
                args.user?.id?.let { id ->
                    viewModel.updateUser(name, password, viewModel.currentAvt.value, id)
                } ?: run {
                    viewModel.newUser(name, password, viewModel.currentAvt.value ?: 1)
                }
                findNavController().popBackStack()
            }
            args.user?.let { user ->
                delete.isVisible = true
                delete.setOnClickListener {
                    if (user.isMainUser) {
                        DeleteDialog(
                            title = R.string.can_not_delete_main_user,
                            yes = R.string.ok,
                            onYes = {}
                        ).show(childFragmentManager, DeleteDialog.TAG)
                    } else {
                        DeleteDialog(
                            title = R.string.delete_user_title,
                            decs = R.string.delete_user_decs,
                            yes = R.string.delete,
                            no = R.string.cancel,
                            onYes = {
                                if (user.isActive) LoginData.account?.users?.find { it.isMainUser }?.isActive =
                                    true
                                viewModel.deleteUser(user.id.toString())
                                findNavController().popBackStack()
                            },
                            onNo = { }
                        ).show(childFragmentManager, DeleteDialog.TAG)
                    }

                }
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
        viewModel.otherMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

}