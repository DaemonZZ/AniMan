package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentChooseModeBinding
import com.daemonz.animange.util.AppModeEnum
import com.daemonz.animange.viewmodel.ChooseModeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ModeFragment : BaseFragment<FragmentChooseModeBinding, ChooseModeViewModel>(
    FragmentChooseModeBinding::inflate,
) {
    override val viewModel: ChooseModeViewModel by viewModels()
    override fun setupViews() {
        binding.apply {
            movieBtn.setOnClickListener {
                viewModel.chooseMode(AppModeEnum.Movies)
                findNavController().navigate(ModeFragmentDirections.actionModeFragmentToHomeFragment())
            }
            mangaBnt.setOnClickListener {
                viewModel.chooseMode(AppModeEnum.Manga)
                findNavController().navigate(ModeFragmentDirections.actionModeFragmentToHomeFragment())
            }
        }
    }

    override fun setupObservers() {

    }
}