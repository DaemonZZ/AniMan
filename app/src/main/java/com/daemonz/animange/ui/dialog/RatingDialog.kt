package com.daemonz.animange.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import com.daemonz.animange.R
import com.daemonz.animange.databinding.DialogRatingBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RatingDialog : BottomSheetDialogFragment() {
    private var _binding: DialogRatingBinding? = null
    private val binding get() = _binding!!
    private var listStar: List<AppCompatImageView> = listOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogRatingBinding.inflate(inflater, container, false)
        BottomSheetBehavior.from(binding.layoutSheet).apply {
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.apply {
            listStar = listOf(
                start1, start2, start3, start4, start5
            )
            listStar.forEachIndexed { index, star ->
                star.setOnClickListener {
                    onStarClicked(index)
                }
            }
            btnYes.setOnClickListener {

            }
        }
        return binding.root
    }

    private fun onStarClicked(index: Int) {
        listStar.forEachIndexed { i, star ->
            if (i <= index) {
                star.setImageResource(R.drawable.star_filled)
            } else {
                star.setImageResource(R.drawable.star_outline)
            }
        }
    }

    companion object {
        const val TAG = "UpdateDialog"
    }
}