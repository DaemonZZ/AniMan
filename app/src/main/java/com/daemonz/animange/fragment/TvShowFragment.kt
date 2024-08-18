package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.daemonz.animange.NavGraphDirections
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentGridListBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.GridAdapter
import com.daemonz.animange.viewmodel.TvShowViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TvShowFragment :
    BaseFragment<FragmentGridListBinding, TvShowViewModel>(FragmentGridListBinding::inflate) {
    override val viewModel: TvShowViewModel by viewModels()
    private val onItemClickListener =
        OnItemClickListener<PagingData<Item>> { item, index ->
            ALog.i(TAG, "onItemClick: $index, status: ${item.data.status}")
            navigateToPlayer(item.data)
        }

    private var tvAdapter: GridAdapter? = null

    override fun setupViews() {
        binding.apply {
            moviesRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
            tvAdapter = GridAdapter(onItemClickListener, currentTheme)
            moviesRecycler.adapter = tvAdapter
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
                        ALog.d(TAG, "load new page ${(tvAdapter?.lastPage ?: -88) + 1}")
                        tvAdapter?.lastPage?.let {
                            viewModel.getTvShows(it + 1)
                            showLoadingOverlay()
                        }
                    }
                }
            })
        }

    }

    override fun setupObservers() {
        viewModel.shows.observe(viewLifecycleOwner) {
            populateData(it)
        }
    }
    override fun initData() {
        viewModel.getTvShows(1)
        showLoadingOverlay()
    }

    private fun navigateToPlayer(item: Item) {
        ALog.i(TAG, "navigateToPlayer: $item")
        findNavController().navigate(NavGraphDirections.actionGlobalPlayerFragment(item = item.slug))
    }

    override fun syncTheme() {
        super.syncTheme()
        setupViews()
        viewModel.shows.value?.let { data ->
            populateData(data)
        }
    }

    private fun populateData(data: List<PagingData<Item>>) {
        ALog.d(TAG, "getTvShows: ${data.size}")
        tvAdapter?.apply {
            setData(data, viewModel.imgDomain)
            ALog.d(TAG, "lastPosition: $lastPosition")
            binding.moviesRecycler.scrollToPosition(lastPosition)
        }
        binding.root.postDelayed({ hideLoadingOverlay() }, 1000)
    }
}