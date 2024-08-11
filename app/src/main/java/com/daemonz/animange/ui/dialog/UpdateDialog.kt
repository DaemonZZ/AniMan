package com.daemonz.animange.ui.dialog

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.Log
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

class UpdateDialog : BottomSheetDialogFragment() {
    private var _binding: UpdateBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var decs = R.string.update_sheet_decs
    private var theme: AnimanTheme? = null
    var isOptional: Boolean = false
        set(value) {
            ALog.d(TAG, "isOptional: $value")
            field = value
            decs = if (value) R.string.update_sheet_decs else R.string.update_sheet_decs_required
        }


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
            btnYes.setOnClickListener {
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$${BuildConfig.APPLICATION_ID}")
                        )
                    )
                }
            }
            btnNo.setOnClickListener {
                dismiss()
            }
            textDesc.text = getString(decs)
            btnNo.isVisible = isOptional
            theme?.let { syncTheme(it) }
        }
        return binding.root
    }

    private fun syncTheme(theme: AnimanTheme) {
        _binding?.apply {
            if (isAdded) {
                root.setBackgroundResource(theme.bottomSheetBg())
                textTitle.setTextColor(theme.firstActivityTextColor(requireContext()))
                textDesc.setTextColor(theme.firstActivityTextColor(requireContext()))
                btnYes.setTextColor(theme.iconTextColor(requireContext()))
                btnYes.setBackgroundColor(theme.firstActivityIconColor(requireContext()))
                btnNo.setTextColor(theme.firstActivityTextColor(requireContext()))
                btnNo.strokeColor =
                    ColorStateList.valueOf(theme.firstActivityTextColor(requireContext()))
            }
        }
    }

    fun setTheme(theme: AnimanTheme) {
        if (this.theme?.equals(theme) != true) {
            this.theme = theme
            syncTheme(theme)
        }
    }

    companion object {
        const val TAG = "UpdateDialog"
    }
}