package com.daemonz.animange.fragment

import androidx.core.view.children
import androidx.fragment.app.viewModels
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
    override val viewModel: SearchViewModel by viewModels()

    override fun setupViews() {
        (activity as? MainActivity)?.setTitle(getString(R.string.filter))
        binding.apply {
            chipCate.setChipSpacing(requireContext().dpToPx(4))
            chipCate.setChipSpacingVerticalResource(R.dimen.dp_0)
            chipCountry.setChipSpacing(requireContext().dpToPx(4))
            chipYear.setChipSpacing(requireContext().dpToPx(4))
            chipCountry.setChipSpacingVerticalResource(R.dimen.dp_0)
            chipYear.setChipSpacingVerticalResource(R.dimen.dp_0)
            chipType.setChipSpacing(requireContext().dpToPx(4))
            chipType.setChipSpacingVerticalResource(R.dimen.dp_0)
            chipType.isSelectionRequired = true
            chipType.isSingleSelection = true
            viewModel.allTypes.forEach { type ->
                val chip = Chip(requireContext())
                chip.text = getString(type.title)
                chip.contentDescription = type.value
                chip.isCheckable = true
                chip.setOnCheckedChangeListener { _, isChecked ->
                    ALog.d(TAG, "onCheckedChange: $isChecked")
                    chip.isChecked = isChecked
                    chip.isChipIconVisible = chip.isChecked
                }
                chipType.addView(chip)
            }
            viewModel.allCategories.forEach { category ->
                val chip = Chip(requireContext())
                chip.text = getString(category.title)
                chip.contentDescription = category.value
                chip.isCheckable = true
                chip.setOnCheckedChangeListener { _, isChecked ->
                    ALog.d(TAG, "onCheckedChange: $isChecked")
                    chip.isChecked = isChecked
                    chip.isChipIconVisible = isChecked
                }
                chipCate.addView(chip)
            }
            viewModel.allCountries.forEach { country ->
                val chip = Chip(requireContext())
                chip.text = getString(country.title)
                chip.contentDescription = country.value
                chip.isCheckable = true
                chip.setOnCheckedChangeListener { _, isChecked ->
                    ALog.d(TAG, "onCheckedChange: $isChecked")
                    chip.isChecked = isChecked
                    chip.isChipIconVisible = isChecked
                }
                chipCountry.addView(chip)
            }
            viewModel.allYears.forEach { year ->
                val chip = Chip(requireContext())
                if (year.value.isEmpty()) {
                    chip.text = getString(R.string.phim_all)
                } else {
                    chip.text = year.value
                }
                chip.contentDescription = year.value
                chip.isCheckable = true
                chip.setOnCheckedChangeListener { _, isChecked ->
                    ALog.d(TAG, "onCheckedChange: $isChecked")
                    chip.isChecked = isChecked
                    chip.isChipIconVisible = isChecked
                }
                chipYear.addView(chip)
            }
            btnApply.setOnClickListener {

            }
        }

    }

    override fun setupObservers() {

    }

    override fun syncTheme() {
        super.syncTheme()
        binding.apply {
            lbCate.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            lbTypeList.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            lbCountry.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            lbYear.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            chipType.children.forEach {
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
                    chip.isChipIconVisible = viewModel.selectedType.value == chip.contentDescription
                    chip.isChecked = viewModel.selectedType.value == chip.contentDescription
                }
            }
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
                    chip.isChipIconVisible = viewModel.selectedCategory.map { it.value }
                        .contains(chip.contentDescription)
                    chip.isChecked = chip.isChipIconVisible
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
                    chip.isChipIconVisible =
                        viewModel.selectedCountry.map { it.value }.contains(chip.contentDescription)
                    chip.isChecked = chip.isChipIconVisible
                }
            }
            chipYear.children.forEach {
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
                    chip.isChipIconVisible =
                        viewModel.selectedYear.map { it.value }.contains(chip.contentDescription)
                    chip.isChecked = chip.isChipIconVisible
                }
            }
        }
    }

    private fun applyFilter() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.toggleToolBarShowing(isShow = false)
    }
}