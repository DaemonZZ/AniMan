package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.MainActivity
import com.daemonz.animange.NavGraphDirections
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentGridListBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.AdBannerHandler
import com.daemonz.animange.ui.adapter.GridAdapter
import com.daemonz.animange.util.AdmobConst
import com.daemonz.animange.util.AdmobConstTest
import com.daemonz.animange.viewmodel.MoviesViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoviesFragment :
    BaseFragment<FragmentGridListBinding, MoviesViewModel>(FragmentGridListBinding::inflate),
    AdBannerHandler {
    override val viewModel: MoviesViewModel by viewModels()
    private val onItemClickListener =
        OnItemClickListener<PagingData<Item>> { item, index ->
            ALog.i(TAG, "onItemClick: $index, status: ${item.data.status}")
            navigateToPlayer(item.data)
        }
    private var adView: AdView? = null
    private var moviesAdapter: GridAdapter? = null

    override fun setupViews() {
        binding.apply {
            moviesRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
            moviesAdapter = GridAdapter(onItemClickListener, currentTheme)
            moviesRecycler.adapter = moviesAdapter
            moviesRecycler.addOnScrollListener(object : OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    ALog.i(TAG, "onScrollStateChanged: state: $newState")
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        toggleToolBarShowing(
                            isShow = true,
                            autoHide = true
                        )
                    }
                    if (!recyclerView.canScrollVertically(1)) {
                        ALog.d(TAG, "load new page ${(moviesAdapter?.lastPage ?: -88) + 1}")
                        moviesAdapter?.lastPage?.let {
                            viewModel.getListMovies(it + 1)
                            showLoadingOverlay()
                        }
                    }
                    if (!recyclerView.canScrollVertically(-1)) {
                        ALog.d(TAG, "load previous page ${(moviesAdapter?.firstPage ?: -88) - 1}")
                        moviesAdapter?.firstPage?.let {
                            if (it > 1) {
                                viewModel.getListMovies(it - 1)
                                showLoadingOverlay()
                            }

                        }
                    }
                }
            })
        }
        setupAdView()
    }

    override fun setupObservers() {
        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            populateData(movies)
        }
    }

    private fun populateData(movies: List<PagingData<Item>>) {
        ALog.d(TAG, "movies: ${movies.size}")
        moviesAdapter?.apply {
            setData(movies, viewModel.imgDomain)
            ALog.d(TAG, "lastPosition: $lastPosition")
            binding.moviesRecycler.scrollToPosition(lastPosition)
        }
        binding.root.postDelayed({ hideLoadingOverlay() }, 1000)
    }

    override fun initData() {
        if (viewModel.movies.value == null) {
            viewModel.getListMovies()
            showLoadingOverlay()
        }
    }

    private fun navigateToPlayer(item: Item) {
        ALog.i(TAG, "navigateToPlayer: $item")
        findNavController().navigate(NavGraphDirections.actionGlobalPlayerFragment(item = item.slug))
    }

    override fun syncTheme() {
        super.syncTheme()
        setupViews()
        viewModel.movies.value?.let { movies ->
            populateData(movies)
        }
    }

    override fun loadBanner() {
        ALog.v(TAG, "loadBanner")
        adView?.adUnitId =
            if (BuildConfig.BUILD_TYPE == "release") AdmobConst.BANNER_AD_ADAPTIVE_2 else AdmobConstTest.BANNER_AD_ADAPTIVE
        adView?.setAdSize(adSize)

        val adRequest = AdRequest.Builder().build()

        ALog.v(TAG, "adRequest:isTestDevice: ${adRequest.isTestDevice(requireContext())}")

        adView?.loadAd(adRequest)
    }

    override fun setupAdView() {
        ALog.d(TAG, "setupAdView loadBanner")
        adView = null
        adView = AdView(requireContext())
        binding.adViewContainer.addView(adView)
        binding.adViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
            if ((activity as? MainActivity)?.isReadyToLoadBanner() == true && adView == null) {
                loadBanner()
            }
        }
        loadBanner()
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