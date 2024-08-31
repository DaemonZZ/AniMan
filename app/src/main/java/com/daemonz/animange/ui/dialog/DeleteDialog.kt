package com.daemonz.animange.ui.dialog

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.R
import com.daemonz.animange.databinding.UpdateBottomSheetBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DeleteDialog(
    private val title: Int,
    private val decs: Int? = null,
    private val yes: Int,
    private val no: Int? = null,
    private val onYes: () -> Unit,
    private val onNo: (() -> Unit)? = null,
    private val theme: AnimanTheme,
) : BottomSheetDialogFragment() {
    private var _binding: UpdateBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UpdateBottomSheetBinding.inflate(inflater, container, false)
        BottomSheetBehavior.from(binding.layoutSheet).apply {
            state = BottomSheetBehavior.STATE_EXPANDED
        }
        dialog?.setCancelable(false)
        binding.apply {
            root.setBackgroundResource(theme.bottomSheetBg())
            btnYes.setTextColor(theme.firstActivityBackgroundColor(requireContext()))
            textTitle.setTextColor(theme.firstActivityTextColor(requireContext()))
            textDesc.setTextColor(theme.firstActivityTextColor(requireContext()))
            btnNo.setTextColor(theme.firstActivityTextColor(requireContext()))
            btnYes.text = getString(yes)
            no?.let { btnNo.text = getString(it) } ?: run { btnNo.isVisible = false }
            textTitle.text = getString(title)
            btnYes.setOnClickListener {
                onYes.invoke()
                dismiss()
            }
            btnNo.setOnClickListener {
                onNo?.invoke()
                dismiss()
            }
            decs?.let { textDesc.text = getString(it) } ?: run { textDesc.isVisible = false }
        }
        return binding.root
    }


    companion object {
        const val TAG = "DeleteDialog"
    }
}