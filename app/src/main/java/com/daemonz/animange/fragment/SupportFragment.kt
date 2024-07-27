package com.daemonz.animange.fragment

import android.content.res.ColorStateList
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.daemonz.animange.MainActivity
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentSuportBinding
import com.daemonz.animange.entity.FeedBack
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.viewmodel.SupportViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupportFragment :
    BaseFragment<FragmentSuportBinding, SupportViewModel>(FragmentSuportBinding::inflate) {
    override val viewModel: SupportViewModel by viewModels()

    override fun setupViews() {
        (activity as? MainActivity)?.setTitle(getString(R.string.support))
        binding.apply {
            edtName.setText(LoginData.account?.name.toString())
            btnSend.isEnabled = false
            edtName.doOnTextChanged { _, _, _, _ ->
                btnSend.isEnabled =
                    !(edtContent.text.isNullOrEmpty() || edtName.text.isNullOrEmpty() || edtEmail.text.isNullOrEmpty())
            }
            edtEmail.setText(LoginData.account?.email.toString())
            edtEmail.doOnTextChanged { _, _, _, _ ->
                btnSend.isEnabled =
                    !(edtContent.text.isNullOrEmpty() || edtName.text.isNullOrEmpty() || edtEmail.text.isNullOrEmpty())
            }
            edtContent.doOnTextChanged { _, _, _, _ ->
                btnSend.isEnabled =
                    !(edtContent.text.isNullOrEmpty() || edtName.text.isNullOrEmpty() || edtEmail.text.isNullOrEmpty())
            }
            btnSend.setOnClickListener {
                val feedback = FeedBack(
                    name = edtName.text.toString(),
                    email = edtEmail.text.toString(),
                    content = edtContent.text.toString(),
                )
                viewModel.sendFeedback(feedback)
            }
        }
    }

    override fun setupObservers() {
        viewModel.feedback.observe(viewLifecycleOwner) {
            if (it == true) {
                Toast.makeText(
                    requireContext().applicationContext,
                    getString(R.string.feedback_success),
                    Toast.LENGTH_LONG
                ).show()
                findNavController().popBackStack()
            } else if (it == false) {
                Toast.makeText(
                    requireContext().applicationContext,
                    getString(R.string.feedback_fail),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun syncTheme() {
        super.syncTheme()
        binding.apply {
            edtName.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            edtEmail.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            edtContent.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
        }
    }
}