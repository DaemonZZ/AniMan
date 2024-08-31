package com.daemonz.animange.fragment

import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentPinBinding
import com.daemonz.animange.util.STRING_EMPTY
import com.daemonz.animange.viewmodel.PinInputViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PinInputFragment :
    BaseFragment<FragmentPinBinding, PinInputViewModel>(FragmentPinBinding::inflate) {
    override val viewModel: PinInputViewModel by viewModels()
    private val arg: PinInputFragmentArgs by navArgs()

    override fun setupViews() {
        binding.pinView.doAfterTextChanged { text ->
            if (text?.length == 6) {
                if (text.toString() == arg.user.password) {
                    findNavController().navigate(
                        PinInputFragmentDirections.actionPinInputFragmentToNavProfile(
                            arg.user
                        )
                    )
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
    }

    override fun setupObservers() {
    }

    override fun syncTheme() {
        super.syncTheme()
        binding.apply {
            lbPin.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            pinView.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            pinView.setLineColor(currentTheme.firstActivityTextColor(requireContext()))
        }
    }
}