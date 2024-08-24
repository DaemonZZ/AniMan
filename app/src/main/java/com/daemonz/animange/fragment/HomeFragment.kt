package com.daemonz.animange.fragment

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.daemonz.animange.NavGraphDirections
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentHomeBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.CommonRecyclerAdapter
import com.daemonz.animange.ui.adapter.FilmCarouselAdapter
import com.daemonz.animange.ui.adapter.HomeCarouselAdapter
import com.daemonz.animange.ui.view_helper.CirclePagerIndicatorDecoration
import com.daemonz.animange.viewmodel.HomeViewModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.MultiBrowseCarouselStrategy
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(FragmentHomeBinding::inflate) {
    override val viewModel: HomeViewModel by activityViewModels()
    private var homeCarouselAdapter: HomeCarouselAdapter? = null
    private var seriesIncomingAdapter: FilmCarouselAdapter? = null
    private var vietNamAdapter: FilmCarouselAdapter? = null
    private var animeAdapter: CommonRecyclerAdapter? = null
    private var moviesAdapter: FilmCarouselAdapter? = null
    private var tvAdapter: CommonRecyclerAdapter? = null


    private val onItemClickListener =
        OnItemClickListener<Item> { item, index ->
            ALog.i(TAG, "onItemClick: $index, status: ${item.status}")
            navigateToPlayer(item)
        }

    override fun setupViews() {
        setupHomeItemRecycler()
        setupNewFilmRecycler()
        setupVietNamRecycler()
        setupAnimeRecycler()
        setupMovieRecycler()
        setupTvRecycler()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // disable swipe back gesture
            }
        })
    }

    private fun setupTvRecycler() {
        binding.apply {
            tvAdapter = CommonRecyclerAdapter(onItemClickListener, currentTheme)
            tvRecycler.adapter = tvAdapter
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
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val view = snapHelper.findSnapView(recyclerView.layoutManager)
                        val title = view?.findViewById<MaterialTextView>(R.id.title)
                        title?.let {
                            ALog.d(TAG, "onScrolled: ${it.text}")
                            titleSeries.text = it.text
                        }
                    }
                }

            })
        }
    }

    private fun setupHomeItemRecycler() {
        binding.apply {
            val snapHelper = PagerSnapHelper()
            homeItemRecycler.onFlingListener = null
            snapHelper.attachToRecyclerView(homeItemRecycler)
            homeCarouselAdapter = HomeCarouselAdapter(
                theme = currentTheme,
                onItemClickListener = { item, index ->
                    ALog.i(TAG, "onItemClick: $index")
                    navigateToPlayer(item)
                }
            )
            homeItemRecycler.adapter = homeCarouselAdapter
            homeItemRecycler.addItemDecoration(
                CirclePagerIndicatorDecoration(
                    colorInactive = currentTheme.indicatorInactive(requireContext()),
                    colorActive = currentTheme.indicatorActive(requireContext())
                )
            )
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
                ALog.d(TAG, "seriesIncoming:")
                hideLoadingOverlay()
                seriesIncomingAdapter?.setData(films.data.items, films.data.imgDomain)
                binding.titleSeries.text = films.data.items.first().name
                binding.titleSeries.requestFocus()
            }
            vietNamFilm.observe(viewLifecycleOwner) { films ->
                ALog.d(TAG, "vietNamFilm: ${films.data.getListUrl()}")
                hideLoadingOverlay()
                vietNamAdapter?.setData(films.data.items, films.data.imgDomain)
                binding.titleVn.text = films.data.items.first().name
                binding.titleVn.requestFocus()
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
            }
            tvShows.observe(viewLifecycleOwner) { films ->
                ALog.d(TAG, "tvShows:")
                hideLoadingOverlay()
                tvAdapter?.setData(films.data.items, films.data.imgDomain)
            }
            allSeries.observe(viewLifecycleOwner) {
                ALog.d(TAG, "allSeries: ")
                hideLoadingOverlay()
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
        viewModel.getAllSeries()
    }

    override fun syncTheme() {
        super.syncTheme()
        binding.apply {
            setupAnimeRecycler()
            setupTvRecycler()
            setupHomeItemRecycler()
            tabAnime.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            tabTv.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            tabMovies.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            tabSeries.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            tabVietNam.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            titleSeries.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            seriesRate.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            titleVn.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            vnRate.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            titleMovies.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            moviesRate.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
        }
    }

}