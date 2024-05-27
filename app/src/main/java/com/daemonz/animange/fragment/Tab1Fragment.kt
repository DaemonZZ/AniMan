package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentTab1Binding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.CommonAction
import com.daemonz.animange.ui.adapter.HomeCarouselAdapter
import com.daemonz.animange.viewmodel.HomeViewModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.FullScreenCarouselStrategy
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Tab1Fragment : BaseFragment<FragmentTab1Binding, HomeViewModel>(FragmentTab1Binding::inflate), CommonAction {
    override val viewModel: HomeViewModel by viewModels()
    private var homeCarouselAdapter: HomeCarouselAdapter? = null

    override fun setupViews() {
        binding.apply {
            homeItemRecycler.layoutManager = CarouselLayoutManager(FullScreenCarouselStrategy())
            val snapHelper = CarouselSnapHelper()
            homeItemRecycler.onFlingListener = null
            snapHelper.attachToRecyclerView(homeItemRecycler)
            homeCarouselAdapter = HomeCarouselAdapter(object : OnItemClickListener<Item> {
                override fun onItemClick(item:Item, index: Int) {
                    ALog.i(TAG, "onItemClick: $index")
                    navigateToPlayer(item)
                }
            })
            homeItemRecycler.adapter = homeCarouselAdapter
            //Indicator not good
//            homeItemRecycler.addItemDecoration(CirclePagerIndicatorDecoration())
        }
    }

    private fun navigateToPlayer(item: Item) {
        ALog.i(TAG, "navigateToPlayer: $item")
        findNavController().navigate(Tab1FragmentDirections.actionTab1FragmentToPlayerFragment(item = item.slug))
    }

    override fun setupObservers() {
        viewModel.apply {
            listDataData.observe(viewLifecycleOwner) { home ->
                ALog.i(TAG, "setupObservers: ${home.data.getListUrl()}")
                homeCarouselAdapter?.setData(home.data.items, home.data.imgDomain)
            }
        }
    }

    override fun initData() {
        viewModel.getHomeData()
    }

    override fun onRefresh() {

    }

    override fun onReSelectBottomNavigationItem(itemId: Int) {

    }

}