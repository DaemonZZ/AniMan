package com.daemonz.animange.fragment

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daemonz.animange.NavGraphDirections
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentHomeBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.BottomNavigationAction
import com.daemonz.animange.ui.adapter.CommonRecyclerAdapter
import com.daemonz.animange.ui.adapter.FilmCarouselAdapter
import com.daemonz.animange.ui.adapter.HomeCarouselAdapter
import com.daemonz.animange.ui.dialog.SearchDialog
import com.daemonz.animange.viewmodel.HomeViewModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.FullScreenCarouselStrategy
import com.google.android.material.carousel.MultiBrowseCarouselStrategy
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(FragmentHomeBinding::inflate),
    BottomNavigationAction {
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
    private val onSearchItemClickListener =
        OnItemClickListener<PagingData<Item>> { item, index ->
            ALog.i(TAG, "onItemClick: $index, status: ${item.data.status}")
            navigateToPlayer(item.data)
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
        }
    }

    private fun setupAnimeRecycler() {
        binding.apply {
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
        }
    }

    private fun setupHomeItemRecycler() {
        binding.apply {
            homeItemRecycler.layoutManager = CarouselLayoutManager(FullScreenCarouselStrategy())
            val snapHelper = CarouselSnapHelper()
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
            val metric = requireActivity().windowManager.currentWindowMetrics.bounds
            val height = metric.width().coerceAtMost(metric.height()) //height
            val params = homeItemRecycler.layoutParams
            params.height = (height * 90) / 100
            homeItemRecycler.layoutParams = params
            ALog.i(TAG, "height homeItemRecycler: ${homeItemRecycler.layoutParams.height}")
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
                homeCarouselAdapter?.setData(home.data.items, home.data.imgDomain)
            }
            seriesIncoming.observe(viewLifecycleOwner) { films ->
                ALog.d(TAG, "seriesIncoming:")
                hideLoadingOverlay()
                seriesIncomingAdapter?.setData(films.data.items, films.data.imgDomain)
            }
            vietNamFilm.observe(viewLifecycleOwner) { films ->
                ALog.d(TAG, "vietNamFilm: ${films.data.getListUrl()}")
                hideLoadingOverlay()
                vietNamAdapter?.setData(films.data.items, films.data.imgDomain)
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

    override fun onSearch() {
        SearchDialog(onSearchItemClickListener, currentTheme).show(
            childFragmentManager,
            "SearchDialog"
        )
    }

    override fun onRefresh() {
        //TODO
    }

    override fun onReSelectBottomNavigationItem(itemId: Int) {
        //TODO
    }

    override fun syncTheme() {
        super.syncTheme()
        binding.apply {
            tabAnime.setBackgroundColor(currentTheme.firstActivityBackgroundColor(requireContext()))
            tabTv.setBackgroundColor(currentTheme.firstActivityBackgroundColor(requireContext()))
            tabMovies.setBackgroundColor(currentTheme.firstActivityBackgroundColor(requireContext()))
            tabSeries.setBackgroundColor(currentTheme.firstActivityBackgroundColor(requireContext()))
            tabVietNam.setBackgroundColor(currentTheme.firstActivityBackgroundColor(requireContext()))
            tabAnime.setTabTextColors(
                currentTheme.tabTextColorDefault(requireContext()),
                currentTheme.tabTextColorSelected(requireContext())
            )
        }
    }

}