package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentTab1Binding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.HomeCarouselAdapter
import com.daemonz.animange.ui.view_helper.CirclePagerIndicatorDecoration
import com.daemonz.animange.viewmodel.HomeViewModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.FullScreenCarouselStrategy
import com.google.android.material.carousel.HeroCarouselStrategy
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Tab1Fragment : BaseFragment<FragmentTab1Binding,HomeViewModel>(FragmentTab1Binding::inflate) {
    override val viewModel: HomeViewModel by viewModels()
    private var homeCarouselAdapter: HomeCarouselAdapter? = null

    override fun setupViews() {
        binding.apply {
            homeItemRecycler.layoutManager = CarouselLayoutManager(FullScreenCarouselStrategy())
            val snapHelper = CarouselSnapHelper()
            homeItemRecycler.onFlingListener = null
            snapHelper.attachToRecyclerView(homeItemRecycler)
            homeCarouselAdapter = HomeCarouselAdapter(object : OnItemClickListener {
                override fun onItemClick(index: Int) {
                    ALog.i(TAG, "onItemClick: $index")
                    val parent = (parentFragment?.parentFragment as? HomeFragment)
                    navigateToPlayer()
                }
            })
            homeItemRecycler.adapter = homeCarouselAdapter
            //Indicator not good
//            homeItemRecycler.addItemDecoration(CirclePagerIndicatorDecoration())
        }
    }

    private fun navigateToPlayer() {
        findNavController().navigate(Tab1FragmentDirections.actionTab1FragmentToPlayerFragment())
    }

    override fun setupObservers() {
        viewModel.apply {
            homeData.observe(viewLifecycleOwner) { home ->
                ALog.i(TAG, "setupObservers: $home")
                homeCarouselAdapter?.setData(home.data.seoOnPage.getListUrl())
            }
        }
    }

    override fun initData() {
        viewModel.getHomeData()
    }

}