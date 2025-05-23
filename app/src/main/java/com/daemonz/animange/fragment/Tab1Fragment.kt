package com.daemonz.animange.fragment

import android.graphics.Rect
import android.os.CancellationSignal
import android.view.ScrollCaptureCallback
import android.view.ScrollCaptureSession
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.dynamicfeatures.Constants
import androidx.navigation.fragment.findNavController
import com.daemonz.animange.MainActivity
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentTab1Binding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.BottomNavigationAction
import com.daemonz.animange.ui.CommonAction
import com.daemonz.animange.ui.adapter.CommonRecyclerAdapter
import com.daemonz.animange.ui.adapter.FilmCarouselAdapter
import com.daemonz.animange.ui.adapter.HomeCarouselAdapter
import com.daemonz.animange.ui.dialog.SearchDialog
import com.daemonz.animange.util.AppUtils
import com.daemonz.animange.util.ITEM_STATUS_TRAILER
import com.daemonz.animange.viewmodel.HomeViewModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.FullScreenCarouselStrategy
import com.google.android.material.carousel.HeroCarouselStrategy
import com.google.android.material.carousel.MultiBrowseCarouselStrategy
import com.google.android.material.carousel.UncontainedCarouselStrategy
import dagger.hilt.android.AndroidEntryPoint
import java.util.function.Consumer

@AndroidEntryPoint
class Tab1Fragment : BaseFragment<FragmentTab1Binding, HomeViewModel>(FragmentTab1Binding::inflate),
    BottomNavigationAction {
    override val viewModel: HomeViewModel by activityViewModels()
    private var homeCarouselAdapter: HomeCarouselAdapter? = null
    private var seriesIncomingAdapter: FilmCarouselAdapter? = null
    private var vietNamAdapter: FilmCarouselAdapter? = null
    private var animeAdapter: CommonRecyclerAdapter? = null
    private var moviesAdapter: FilmCarouselAdapter? = null
    private var tvAdapter: CommonRecyclerAdapter? = null


    private val onItemClickListener = object : OnItemClickListener<Item> {
        override fun onItemClick(item: Item, index: Int) {
            ALog.i(TAG, "onItemClick: $index, status: ${item.status}")
            navigateToPlayer(item)
        }
    }

    override fun setupViews() {
        setupHomeItemRecycler()
        setupNewFilmRecycler()
        setupVietNamRecycler()
        setupAnimeRecycler()
        setupMovieRecycler()
        setupTvRecycler()
    }

    private fun setupTvRecycler() {
        binding.apply {
            tvAdapter = CommonRecyclerAdapter(onItemClickListener)
            tvRecycler.adapter = tvAdapter
        }
    }

    private fun setupMovieRecycler() {
        binding.apply {
            movERecycler.layoutManager = CarouselLayoutManager()
            val snapHelper = CarouselSnapHelper()
            movERecycler.onFlingListener = null
            snapHelper.attachToRecyclerView(movERecycler)
            moviesAdapter = FilmCarouselAdapter(onItemClickListener)
            movERecycler.adapter = moviesAdapter
        }
    }

    private fun setupAnimeRecycler() {
        binding.apply {
            animeAdapter = CommonRecyclerAdapter(onItemClickListener)
            animeRecycler.adapter = animeAdapter
        }
    }

    private fun setupVietNamRecycler() {
        binding.apply {
            vietNamRecycler.layoutManager = CarouselLayoutManager()
            val snapHelper = CarouselSnapHelper()
            vietNamRecycler.onFlingListener = null
            snapHelper.attachToRecyclerView(vietNamRecycler)
            vietNamAdapter = FilmCarouselAdapter(onItemClickListener)
            vietNamRecycler.adapter = vietNamAdapter
        }
    }

    private fun setupNewFilmRecycler() {
        binding.apply {
            seriesRecycler.layoutManager = CarouselLayoutManager(MultiBrowseCarouselStrategy())
            val snapHelper = CarouselSnapHelper()
            seriesRecycler.onFlingListener = null
            snapHelper.attachToRecyclerView(seriesRecycler)
            seriesIncomingAdapter = FilmCarouselAdapter(onItemClickListener)
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
            root.setOnScrollChangeListener { _, _, _, _, _ ->
                toggleToolBarShowing(isShow = true, autoHide = true)
            }
        }
    }

    private fun navigateToPlayer(item: Item) {
        ALog.i(TAG, "navigateToPlayer: $item")
        findNavController().navigate(Tab1FragmentDirections.actionTab1FragmentToPlayerFragment(item = item.slug))
    }

    override fun setupObservers() {
        viewModel.apply {
            listDataData.observe(viewLifecycleOwner) { home ->
                ALog.d(TAG, "listDataData: ${home.data.getListUrl()}")
                hideLoadingOverlay("getHomeData")
                homeCarouselAdapter?.setData(home.data.items, home.data.imgDomain)
            }
            seriesIncoming.observe(viewLifecycleOwner) { films ->
                ALog.d(TAG, "seriesIncoming:")
                hideLoadingOverlay("getSeriesIncoming")
                seriesIncomingAdapter?.setData(films.data.items, films.data.imgDomain)
            }
            vietNamFilm.observe(viewLifecycleOwner) { films ->
                ALog.d(TAG, "vietNamFilm: ${films.data.getListUrl()}")
                hideLoadingOverlay("getListFilmVietNam")
                vietNamAdapter?.setData(films.data.items, films.data.imgDomain)
            }
            anime.observe(viewLifecycleOwner) { films ->
                ALog.d(TAG, "anime: ${films.data.getListUrl()}")
                hideLoadingOverlay("getListAnime")
                animeAdapter?.setData(films.data.items, films.data.imgDomain)
            }
            movies.observe(viewLifecycleOwner) { films ->
                ALog.d(TAG, "movies: ${films.data.getListUrl()}")
                hideLoadingOverlay("getListMovies")
                moviesAdapter?.setData(films.data.items, films.data.imgDomain)
            }
            tvShows.observe(viewLifecycleOwner) { films ->
                ALog.d(TAG, "tvShows:")
                hideLoadingOverlay("getTvShows")
                tvAdapter?.setData(films.data.items, films.data.imgDomain)
            }
            allSeries.observe(viewLifecycleOwner) {
                ALog.d(TAG, "allSeries: ")
                hideLoadingOverlay("getAllSeries")
            }
        }
    }

    override fun initData() {
        viewModel.getHomeData()
        showLoadingOverlay("getHomeData")
        viewModel.getSeriesIncoming()
        showLoadingOverlay("getSeriesIncoming")
        viewModel.getListFilmVietNam()
        showLoadingOverlay("getListFilmVietNam")
        viewModel.getListAnime()
        showLoadingOverlay("getListAnime")
        viewModel.getListMovies()
        showLoadingOverlay("getListMovies")
        viewModel.getTvShows()
        showLoadingOverlay("getTvShows")
        viewModel.getAllSeries()
        showLoadingOverlay("getAllSeries")
    }

    override fun onSearch() {
        SearchDialog(onItemClickListener).show(childFragmentManager, "SearchDialog")
    }

    override fun onRefresh() {
        //TODO
    }

    override fun onReSelectBottomNavigationItem(itemId: Int) {
        //TODO
    }

}