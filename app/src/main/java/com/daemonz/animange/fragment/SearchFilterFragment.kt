package com.daemonz.animange.fragment

import androidx.core.view.children
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.daemonz.animange.MainActivity
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentSearchFilterBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.dpToPx
import com.daemonz.animange.viewmodel.SearchViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFilterFragment :
    BaseFragment<FragmentSearchFilterBinding, SearchViewModel>(FragmentSearchFilterBinding::inflate) {
    override val viewModel: SearchViewModel by hiltNavGraphViewModels(R.id.nav_search)

    override fun setupViews() {
        (activity as? MainActivity)?.setTitle(getString(R.string.filter))
        binding.apply {
            chipCate.setChipSpacing(requireContext().dpToPx(4))
            chipCate.setChipSpacingVerticalResource(R.dimen.dp_0)
            chipCountry.setChipSpacing(requireContext().dpToPx(4))
            chipCountry.setChipSpacingVerticalResource(R.dimen.dp_0)
            viewModel.allCategories.forEach { category ->
                val chip = Chip(requireContext())
                chip.text = getString(category.title)
                chip.isCheckable = true
                chip.setOnCheckedChangeListener { _, isChecked ->
                    ALog.d(TAG, "onCheckedChange: $isChecked")
                    chip.isChipIconVisible = isChecked
                }
                chipCate.addView(chip)
            }
            viewModel.allCountries.forEach { country ->
                val chip = Chip(requireContext())
                chip.text = getString(country.title)
                chip.isCheckable = true
                chip.setOnCheckedChangeListener { _, isChecked ->
                    ALog.d(TAG, "onCheckedChange: $isChecked")
                    chip.isChipIconVisible = isChecked
                }
                chipCountry.addView(chip)
            }
        }

    }

    override fun setupObservers() {

    }

    override fun syncTheme() {
        super.syncTheme()
        binding.apply {
            lbCate.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            lbCountry.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            chipCate.children.forEach {
                (it as? Chip)?.let { chip ->
                    chip.setChipDrawable(
                        ChipDrawable.createFromAttributes(
                            requireContext(),
                            null,
                            0,
                            currentTheme.chipStyle()
                        )
                    )
                    chip.setChipIconResource(R.drawable.ic_checked)
                    chip.isChipIconVisible = false
                }
            }
            chipCountry.children.forEach {
                (it as? Chip)?.let { chip ->
                    chip.setChipDrawable(
                        ChipDrawable.createFromAttributes(
                            requireContext(),
                            null,
                            0,
                            currentTheme.chipStyle()
                        )
                    )
                    chip.setChipIconResource(R.drawable.ic_checked)
                    chip.isChipIconVisible = false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.toggleToolBarShowing(isShow = false)
    }
}