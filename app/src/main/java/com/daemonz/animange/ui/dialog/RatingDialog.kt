package com.daemonz.animange.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import com.daemonz.animange.R
import com.daemonz.animange.databinding.DialogRatingBinding
import com.daemonz.animange.entity.FilmRating
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RatingDialog(
    private val currentRating: FilmRating? = null,
    private val onYes: (Int, String, String?) -> Unit,
) : BottomSheetDialogFragment() {
    private var _binding: DialogRatingBinding? = null
    private val binding get() = _binding!!
    private var listStar: List<AppCompatImageView> = listOf()
    private var userSelected: Int = 0

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
                onYes.invoke(userSelected, comment.text.toString(), currentRating?.id)
                dismiss()
            }
            if (currentRating != null) {
                onStarClicked(currentRating.rating.toInt() - 1)
                comment.setText(currentRating.comment)
            }
        }
        return binding.root
    }

    private fun onStarClicked(index: Int) {
        userSelected = index
        listStar.forEachIndexed { i, star ->
            if (i <= index) {
                star.setImageResource(getFillStarDrawable(i))
            } else {
                star.setImageResource(R.drawable.star_outline)
            }
        }
    }

    private fun getFillStarDrawable(index: Int): Int {
        return when (index) {
            0 -> R.drawable.star_1
            1 -> R.drawable.star_2
            2 -> R.drawable.star_3
            3 -> R.drawable.star_4
            4 -> R.drawable.star_5
            else -> R.drawable.star_outline
        }
    }

    companion object {
        const val TAG = "UpdateDialog"
    }
}