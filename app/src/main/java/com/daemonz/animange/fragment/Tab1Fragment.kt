package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentTab1Binding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.CommonAction
import com.daemonz.animange.ui.adapter.FilmCarouselAdapter
import com.daemonz.animange.ui.adapter.HomeCarouselAdapter
import com.daemonz.animange.viewmodel.HomeViewModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.FullScreenCarouselStrategy
import com.google.android.material.carousel.HeroCarouselStrategy
import com.google.android.material.carousel.MultiBrowseCarouselStrategy
import com.google.android.material.carousel.UncontainedCarouselStrategy
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Tab1Fragment : BaseFragment<FragmentTab1Binding, HomeViewModel>(FragmentTab1Binding::inflate), CommonAction {
    override val viewModel: HomeViewModel by viewModels()
    private var homeCarouselAdapter: HomeCarouselAdapter? = null
    private var seriesIncomingAdapter: FilmCarouselAdapter? = null
    private var vietNamAdapter: FilmCarouselAdapter? = null

    override fun setupViews() {
        setupHomeItemRecycler()
        setupNewFilmRecycler()
        setupVietNamRecycler()
    }

    private fun setupVietNamRecycler() {
        binding.apply {
            vietNamRecycler.layoutManager = CarouselLayoutManager()
            val snapHelper = CarouselSnapHelper()
            vietNamRecycler.onFlingListener = null
            snapHelper.attachToRecyclerView(vietNamRecycler)
            vietNamAdapter = FilmCarouselAdapter(object : OnItemClickListener<Item> {
                override fun onItemClick(item: Item, index: Int) {
                    ALog.i(TAG, "onItemClick: $index")
                    navigateToPlayer(item)
                }
            })
            vietNamRecycler.adapter = vietNamAdapter
        }
    }

    private fun setupNewFilmRecycler() {
        binding.apply {
            seriesRecycler.layoutManager = CarouselLayoutManager(MultiBrowseCarouselStrategy())
            val snapHelper = CarouselSnapHelper()
            seriesRecycler.onFlingListener = null
            snapHelper.attachToRecyclerView(seriesRecycler)
            seriesIncomingAdapter = FilmCarouselAdapter(object : OnItemClickListener<Item> {
                override fun onItemClick(item: Item, index: Int) {
                    ALog.i(TAG, "onItemClick: $index")
                    navigateToPlayer(item)
                }
            })
            seriesRecycler.adapter = seriesIncomingAdapter
        }
    }

    private fun setupHomeItemRecycler() {
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
            val metric = requireActivity().windowManager.currentWindowMetrics.bounds
            val height = metric.width().coerceAtMost(metric.height()) //height
            val params = homeItemRecycler.layoutParams
            params.height = (height * 90) / 100
            homeItemRecycler.layoutParams = params
            ALog.i(TAG, "height homeItemRecycler: ${homeItemRecycler.layoutParams.height}")
            //Indicator not good
            //homeItemRecycler.addItemDecoration(CirclePagerIndicatorDecoration())
        }
    }

    override fun onStart() {
        super.onStart()
        (parentFragment?.parentFragment as? HomeFragment)?.changeToolBarAction(this@Tab1Fragment)
    }

    private fun navigateToPlayer(item: Item) {
        ALog.i(TAG, "navigateToPlayer: $item")
        findNavController().navigate(Tab1FragmentDirections.actionTab1FragmentToPlayerFragment(item = item.slug))
    }

    override fun setupObservers() {
        viewModel.apply {
            listDataData.observe(viewLifecycleOwner) { home ->
                ALog.i(TAG, "listDataData: ${home.data.getListUrl()}")
                homeCarouselAdapter?.setData(home.data.items, home.data.imgDomain)
            }
            seriesIncoming.observe(viewLifecycleOwner) { films ->
                ALog.i(TAG, "seriesIncoming: ${films.data.getListUrl()}")
                seriesIncomingAdapter?.setData(films.data.items, films.data.imgDomain)
            }
            vietNamFilm.observe(viewLifecycleOwner) { films ->
                ALog.i(TAG, "vietNamFilm: ${films.data.getListUrl()}")
                vietNamAdapter?.setData(films.data.items, films.data.imgDomain)
            }
        }
    }

    override fun initData() {
        viewModel.getHomeData()
        viewModel.getSeriesIncoming()
        viewModel.getListFilmVietNam()
    }

    override fun onRefresh() {
        //TODO
    }

    override fun onReSelectBottomNavigationItem(itemId: Int) {
        //TODO
    }

}