package com.daemonz.animange.fragment

import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daemonz.animange.MainActivity
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentPinBinding
import com.daemonz.animange.ui.NavIcon2Action
import com.daemonz.animange.ui.dialog.DeleteDialog
import com.daemonz.animange.util.STRING_EMPTY
import com.daemonz.animange.viewmodel.LoginViewModel
import com.daemonz.animange.viewmodel.PinInputViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PinInputFragment :
    BaseFragment<FragmentPinBinding, PinInputViewModel>(FragmentPinBinding::inflate),
    NavIcon2Action {
    override val viewModel: PinInputViewModel by viewModels()
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val arg: PinInputFragmentArgs by navArgs()

    override fun setupViews() {
        binding.pinView.doAfterTextChanged { text ->
            if (text?.length == 6) {
                if (text.toString() == arg.user.password) {
                    if (arg.isLogin) {
                        if (arg.isSwitchUser) {
                            viewModel.switchUser(arg.user.id.toString())
                        }
                        findNavController().navigate(PinInputFragmentDirections.actionPinInputFragmentToHomeFragment())

                    } else {
                        if (arg.isSwitchUser) {
                            viewModel.switchUser(arg.user.id.toString())
                            findNavController().popBackStack()
                        } else {
                            findNavController().navigate(
                                PinInputFragmentDirections.actionPinInputFragmentToNavProfile(
                                    arg.user,
                                    true
                                )
                            )
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.wrong_password),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.pinView.setText(STRING_EMPTY)
                }
            }
        }
        binding.btnSwitchUser.isVisible = arg.isLogin
        binding.btnSwitchUser.setOnClickListener {
            findNavController().navigate(
                PinInputFragmentDirections.actionPinInputFragmentToChooseUserFragment(
                    arg.isLogin,
                    true
                )
            )
        }
        (activity as? MainActivity)?.setTitle(getString(R.string.input_pin))
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!arg.isLogin) {
                    onBack()
                }
            }
        })

    }

    override fun setupObservers() {
        loginViewModel.account.observe(viewLifecycleOwner) {
            if (it == null) {
                lifecycleScope.launch {
                    delay(1000)
                    hideLoadingOverlay()
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun syncTheme() {
        super.syncTheme()
        binding.apply {
            lbPin.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            pinView.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            pinView.setLineColor(currentTheme.firstActivityTextColor(requireContext()))
        }
    }

    override fun onNavIcon2Click() {
        if (arg.isLogin) {
            DeleteDialog(
                title = R.string.log_out,
                decs = R.string.do_you_want_to_log_out,
                yes = R.string.log_out,
                no = R.string.no,
                onYes = {
                    loginViewModel.logout(requireContext())
                },
                theme = currentTheme
            ).show(childFragmentManager, TAG)
        } else {
            findNavController().popBackStack()
        }
    }
}