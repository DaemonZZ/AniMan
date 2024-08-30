package com.daemonz.animange.fragment

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.MainActivity
import com.daemonz.animange.NavGraphDirections
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentHomeBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.AdmobHandler
import com.daemonz.animange.ui.adapter.CommonRecyclerAdapter
import com.daemonz.animange.ui.adapter.FilmCarouselAdapter
import com.daemonz.animange.ui.adapter.HomeCarouselAdapter
import com.daemonz.animange.ui.view_helper.CirclePagerIndicatorDecoration
import com.daemonz.animange.util.AdmobConst
import com.daemonz.animange.util.AdmobConstTest
import com.daemonz.animange.util.isFavorite
import com.daemonz.animange.util.makeTextLink
import com.daemonz.animange.viewmodel.HomeViewModel
import com.daemonz.animange.viewmodel.PlayerViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.MultiBrowseCarouselStrategy
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(FragmentHomeBinding::inflate), AdmobHandler {
    override val viewModel: HomeViewModel by activityViewModels()
    private val playerViewModel: PlayerViewModel by viewModels()
    private var homeCarouselAdapter: HomeCarouselAdapter? = null
    private var seriesIncomingAdapter: FilmCarouselAdapter? = null
    private var vietNamAdapter: FilmCarouselAdapter? = null
    private var animeAdapter: CommonRecyclerAdapter? = null
    private var moviesAdapter: FilmCarouselAdapter? = null
    private var tvAdapter: CommonRecyclerAdapter? = null
    private var itemDecor: CirclePagerIndicatorDecoration? = null

    private val onItemClickListener =
        OnItemClickListener<Item> { item, index ->
            ALog.i(TAG, "onItemClick: $index, status: ${item.status}")
            navigateToPlayer(item)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ALog.d(TAG, "onCreate")
    }

    override fun setupViews() {
        ALog.d(TAG, "setupView")
        setupNewFilmRecycler()
        setupVietNamRecycler()
        setupAnimeRecycler()
        setupMovieRecycler()
        setupTvRecycler()
        setupAdView()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // disable swipe back gesture
            }
        })
    }

    override fun setupAdView() {
        ALog.d(TAG, "setupAdView loadBanner")
        adView = null
        adView = AdView(requireContext())
        binding.adViewContainer.addView(adView)
        binding.adViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
            if ((activity as MainActivity).isReadyToLoadBanner() && adView == null) {
                loadBanner()
            }
        }
        loadBanner()
    }

    private fun setupTvRecycler() {
        binding.apply {
            tvAdapter = CommonRecyclerAdapter(onItemClickListener, currentTheme)
            tvRecycler.adapter = tvAdapter
        }
        viewModel.tvShows.value?.let { films ->
            ALog.d(TAG, "tvShows:")
            hideLoadingOverlay()
            tvAdapter?.setData(films.data.items, films.data.imgDomain)
        }
    }

    private fun setupMovieRecycler() {
        binding.apply {
            movERecycler.layoutManager = CarouselLayoutManager()
            val snapHelper = CarouselSnapHelper()
            movERecycler.onFlingListener = null
            snapHelper.attachToRecyclerView(movERecycler)
            moviesAdapter = FilmCarouselAdapter(onItemClickListener, currentTheme)
            movERecycler.adapter = moviesAdapter
            movERecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val view = snapHelper.findSnapView(recyclerView.layoutManager)
                        val title = view?.findViewById<MaterialTextView>(R.id.title)
                        title?.let {
                            ALog.d(TAG, "onScrolled: ${it.text}")
                            titleMovies.text = it.text
                        }
                        val rateView = view?.findViewById<MaterialTextView>(R.id.rate)
                        rateView?.let {
                            moviesRate.text = it.text
                        }
                    }
                }

            })
        }
    }

    private fun setupAnimeRecycler() {
        binding.apply {
            val snapHelper = LinearSnapHelper()
            animeRecycler.onFlingListener = null
            snapHelper.attachToRecyclerView(animeRecycler)
            animeAdapter = CommonRecyclerAdapter(onItemClickListener, currentTheme)
            animeRecycler.adapter = animeAdapter
        }
        viewModel.anime.value?.let { films ->
            ALog.d(TAG, "anime: ${films.data.getListUrl()}")
            hideLoadingOverlay()
            animeAdapter?.setData(films.data.items, films.data.imgDomain)
        }
    }

    private fun setupVietNamRecycler() {
        binding.apply {
            vietNamRecycler.layoutManager = CarouselLayoutManager()
            val snapHelper = CarouselSnapHelper()
            vietNamRecycler.onFlingListener = null
            snapHelper.attachToRecyclerView(vietNamRecycler)
            vietNamAdapter = FilmCarouselAdapter(onItemClickListener, currentTheme)
            vietNamRecycler.adapter = vietNamAdapter
            vietNamRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val view = snapHelper.findSnapView(recyclerView.layoutManager)
                        val title = view?.findViewById<MaterialTextView>(R.id.title)
                        title?.let {
                            ALog.d(TAG, "onScrolled: ${it.text}")
                            titleVn.text = it.text
                        }
                        val rateView = view?.findViewById<MaterialTextView>(R.id.rate)
                        rateView?.let {
                            vnRate.text = it.text
                        }
                    }
                }
            })
        }
    }

    private fun setupNewFilmRecycler() {
        binding.apply {
            seriesRecycler.layoutManager = CarouselLayoutManager(MultiBrowseCarouselStrategy())
            val snapHelper = CarouselSnapHelper()
            seriesRecycler.onFlingListener = null
            snapHelper.attachToRecyclerView(seriesRecycler)
            seriesIncomingAdapter = FilmCarouselAdapter(onItemClickListener, currentTheme)
            seriesRecycler.adapter = seriesIncomingAdapter

            seriesRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    try {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            val view = snapHelper.findSnapView(recyclerView.layoutManager)
                            val title = view?.findViewById<MaterialTextView>(R.id.title)
                            title?.let {
                                titleSeries.text = it.text
                            }
                            val rateView = view?.findViewById<MaterialTextView>(R.id.rate)
                            rateView?.let {
                                seriesRate.text = it.text
                            }
                        }
                    } catch (e: Exception) {
                        ALog.e(TAG, "Error on scroll: $e")
                    }

                }

            })
        }
    }

    private fun setupHomeItemRecycler() {
        ALog.d(TAG, "setupHomeItemRecycler")
        binding.apply {
            homeCarouselAdapter = null
            homeItemRecycler.adapter = null
            val snapHelper = PagerSnapHelper()
            homeItemRecycler.onFlingListener = null
            snapHelper.attachToRecyclerView(homeItemRecycler)
            homeCarouselAdapter = HomeCarouselAdapter(
                theme = currentTheme,
                onItemClickListener = { item, index ->
                    ALog.i(TAG, "onItemClick: $index")
                    navigateToPlayer(item)
                },
                onFollowClicked = { item, img ->
                    ALog.i(TAG, "onFollowClick: ${item.isFavorite()}")
                    if (item.isFavorite()) {
                        playerViewModel.unMarkItemAsFavorite(item)
                    } else {
                        playerViewModel.markItemAsFavorite(item, img)
                    }
                }
            )
            homeItemRecycler.adapter = homeCarouselAdapter
            if (itemDecor == null) {
                itemDecor = CirclePagerIndicatorDecoration(
                    colorInactive = currentTheme.indicatorInactive(requireContext()),
                    colorActive = currentTheme.indicatorActive(requireContext())
                )
                homeItemRecycler.addItemDecoration(itemDecor!!)
            }

            val llm = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//            llm.isAutoMeasureEnabled = false
            homeItemRecycler.layoutManager = llm
//            val metric = requireActivity().windowManager.currentWindowMetrics.bounds
//            val height = metric.width().coerceAtMost(metric.height()) //height
//            val params = homeItemRecycler.layoutParams
//            params.height = (height * 90) / 100
//            homeItemRecycler.layoutParams = params
//            ALog.i(TAG, "height homeItemRecycler: ${homeItemRecycler.layoutParams.height}")
            root.setOnScrollChangeListener { _, _, _, _, _ ->
                toggleToolBarShowing(isShow = true, autoHide = true)
            }
        }
        viewModel.listDataData.value?.let { home ->
            ALog.d(TAG, "listDataData: ${home.data.getListUrl()}")
            hideLoadingOverlay()
            homeCarouselAdapter?.setData(home.data.items.take(5), home.data.imgDomain)
        }
    }

    private fun navigateToPlayer(item: Item) {
        ALog.i(TAG, "navigateToPlayer: $item")
        findNavController().navigate(NavGraphDirections.actionGlobalPlayerFragment(item = item.slug))
    }

    override fun setupObservers() {
        viewModel.apply {
            listDataData.observe(viewLifecycleOwner) { home ->
                ALog.d(TAG, "listDataData: ${home.data.getListUrl()}")
                hideLoadingOverlay()
                homeCarouselAdapter?.setData(home.data.items.take(5), home.data.imgDomain)
            }
            seriesIncoming.observe(viewLifecycleOwner) { films ->
                ALog.d(TAG, "seriesIncoming: ${films.data.items.first().rating}")
                hideLoadingOverlay()
                seriesIncomingAdapter?.setData(films.data.items, films.data.imgDomain)
                binding.titleSeries.text = films.data.items.first().name
                binding.titleSeries.requestFocus()
                binding.seriesRate.text = if (films.data.items.first().rating.isNaN())
                    "0.0" else
                    films.data.items.first().rating.toString()
                binding.seriesRate.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    if (films.data.items.first().rating > 0) R.drawable.star_filled_24 else R.drawable.star_outline_18,
                    0
                )
            }
            vietNamFilm.observe(viewLifecycleOwner) { films ->
                ALog.d(TAG, "vietNamFilm: ${films.data.getListUrl()}")
                hideLoadingOverlay()
                vietNamAdapter?.setData(films.data.items, films.data.imgDomain)
                binding.titleVn.text = films.data.items.first().name
                binding.titleVn.requestFocus()
                binding.vnRate.text = if (films.data.items.first().rating.isNaN())
                    "0.0" else
                    films.data.items.first().rating.toString()
                binding.vnRate.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    if (films.data.items.first().rating > 0) R.drawable.star_filled_24 else R.drawable.star_outline_18,
                    0
                )
            }
            anime.observe(viewLifecycleOwner) { films ->
                ALog.d(TAG, "anime: ${films.data.getListUrl()}")
                hideLoadingOverlay()
                animeAdapter?.setData(films.data.items, films.data.imgDomain)
            }
            movies.observe(viewLifecycleOwner) { films ->
                ALog.d(TAG, "movies: ${films.data.getListUrl()}")
                hideLoadingOverlay()
                moviesAdapter?.setData(films.data.items, films.data.imgDomain)
                binding.titleMovies.text = films.data.items.first().name
                binding.titleMovies.requestFocus()
                binding.moviesRate.text = if (films.data.items.first().rating.isNaN())
                    "0.0" else
                    films.data.items.first().rating.toString()
                binding.moviesRate.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    if (films.data.items.first().rating > 0) R.drawable.star_filled_24 else R.drawable.star_outline_18,
                    0
                )
            }
            tvShows.observe(viewLifecycleOwner) { films ->
                ALog.d(TAG, "tvShows:")
                hideLoadingOverlay()
                tvAdapter?.setData(films.data.items, films.data.imgDomain)
            }
        }
    }

    override fun initData() {
        viewModel.getHomeData()
        viewModel.getSeriesIncoming()
        viewModel.getListFilmVietNam()
        viewModel.getListAnime()
        viewModel.getListMovies()
        viewModel.getTvShows()
    }

    override fun syncTheme() {
        ALog.d(TAG, "syncTheme")
        super.syncTheme()
        binding.apply {
            setupAnimeRecycler()
            setupTvRecycler()
            setupHomeItemRecycler()
            tabAnime.makeTextLink(
                textLink = tabAnime.text.toString(),
                underline = true,
                color = currentTheme.firstActivityTextColor(requireContext())
            )
            tabTv.makeTextLink(
                textLink = tabTv.text.toString(),
                underline = true,
                color = currentTheme.firstActivityTextColor(requireContext())
            )
            tabMovies.makeTextLink(
                textLink = tabMovies.text.toString(),
                underline = true,
                color = currentTheme.firstActivityTextColor(requireContext())
            )
            tabSeries.makeTextLink(
                textLink = tabSeries.text.toString(),
                underline = true,
                color = currentTheme.firstActivityTextColor(requireContext())
            )
            tabVietNam.makeTextLink(
                textLink = tabVietNam.text.toString(),
                underline = true,
                color = currentTheme.firstActivityTextColor(requireContext())
            )
            titleSeries.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            seriesRate.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            titleVn.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            vnRate.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            titleMovies.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            moviesRate.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
        }
    }

    private var adView: AdView? = null
    override fun loadBanner() {
        ALog.v(TAG, "loadBanner")
        adView?.adUnitId =
            if (BuildConfig.BUILD_TYPE == "release") AdmobConst.BANNER_AD_ADAPTIVE else AdmobConstTest.BANNER_AD_ADAPTIVE
        adView?.setAdSize(adSize)

        val adRequest = AdRequest.Builder().build()

        ALog.v(TAG, "adRequest:isTestDevice: ${adRequest.isTestDevice(requireContext())}")

        adView?.loadAd(adRequest)
    }

    override fun onResume() {
        super.onResume()
        adView?.resume()
    }

    override fun onPause() {
        super.onPause()
        adView?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        adView?.destroy()
    }

}