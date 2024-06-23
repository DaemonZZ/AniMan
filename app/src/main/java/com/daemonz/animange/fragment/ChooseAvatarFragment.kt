package com.daemonz.animange.fragment

import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentChooseAvtBinding
import com.daemonz.animange.ui.adapter.AvatarGridAdapter
import com.daemonz.animange.viewmodel.ProfileViewModel
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
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
            avtRecycler.layoutManager = FlexboxLayoutManager(requireContext()).apply {
                justifyContent = JustifyContent.CENTER
            }
            avtAdapter = AvatarGridAdapter(onItemClickListener)
            avtRecycler.adapter = avtAdapter
            avtAdapter?.setData((1..21).map { it })
            avtRecycler.addOnScrollListener(object : OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    toggleToolBarShowing(isShow = true, autoHide = true)
                    super.onScrollStateChanged(recyclerView, newState)
                }
            })
        }
    }

    override fun setupObservers() {

    }
}