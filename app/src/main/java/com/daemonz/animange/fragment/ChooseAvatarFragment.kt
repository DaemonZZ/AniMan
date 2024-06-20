package com.daemonz.animange.fragment

import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentChooseAvtBinding
import com.daemonz.animange.ui.adapter.AvatarGridAdapter
import com.daemonz.animange.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseAvatarFragment :
    BaseFragment<FragmentChooseAvtBinding, ProfileViewModel>(FragmentChooseAvtBinding::inflate) {
    override val viewModel: ProfileViewModel by hiltNavGraphViewModels(R.id.nav_profile)


    private var avtAdapter: AvatarGridAdapter? = null
    private val onItemClickListener =
        OnItemClickListener<Int> { item, _ ->
            viewModel.chooseAvt(item)
            findNavController().popBackStack()
        }

    override fun setupViews() {
        binding.apply {
            avtRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
            avtAdapter = AvatarGridAdapter(onItemClickListener)
            avtRecycler.adapter = avtAdapter
            avtAdapter?.setData((1..20).map { it })
        }
    }

    override fun setupObservers() {

    }
}