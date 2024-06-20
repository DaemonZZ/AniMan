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

    private val avatars = listOf(
        R.drawable.avt_1,
        R.drawable.avt_2,
        R.drawable.avt_3,
        R.drawable.avt_4,
        R.drawable.avt_5,
        R.drawable.avt_6,
        R.drawable.avt_7,
        R.drawable.avt_8,
        R.drawable.avt_9,
        R.drawable.avt_10,
        R.drawable.avt_11,
        R.drawable.avt_12,
        R.drawable.avt_13,
        R.drawable.avt_14,
        R.drawable.avt_15,
        R.drawable.avt_16,
        R.drawable.avt_17,
        R.drawable.avt_18,
        R.drawable.avt_19,
        R.drawable.avt_20,
    )

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
            avtAdapter?.setData(avatars)
        }
    }

    override fun setupObservers() {

    }
}