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
import com.daemonz.animange.databinding.DialogRatingBinding
import com.daemonz.animange.log.ALog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RatingDialog : BottomSheetDialogFragment() {
    private var _binding: DialogRatingBinding? = null
    private val binding get() = _binding!!
    private var decs = R.string.update_sheet_decs
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
        _binding = DialogRatingBinding.inflate(inflater, container, false)
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
        }
        return binding.root
    }

    companion object {
        const val TAG = "UpdateDialog"
    }
}