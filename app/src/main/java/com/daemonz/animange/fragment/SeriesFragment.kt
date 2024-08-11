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
import com.daemonz.animange.viewmodel.SeriesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeriesFragment :
    BaseFragment<FragmentGridListBinding, SeriesViewModel>(FragmentGridListBinding::inflate) {
    override val viewModel: SeriesViewModel by viewModels()
    private val onItemClickListener =
        OnItemClickListener<PagingData<Item>> { item, index ->
            ALog.i(TAG, "onItemClick: $index, status: ${item.data.status}")
            navigateToPlayer(item.data)
        }

    private var seriesAdapter: GridAdapter? = null

    override fun setupViews() {
        binding.apply {
            seriesAdapter = GridAdapter(onItemClickListener, currentTheme)
            moviesRecycler.adapter = seriesAdapter
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
                        ALog.d(TAG, "load new page ${(seriesAdapter?.lastPage ?: -88) + 1}")
                        seriesAdapter?.lastPage?.let {
                            viewModel.getAllSeries(it + 1)
                            showLoadingOverlay()
                        }
                    }
                    if (!recyclerView.canScrollVertically(-1)) {
                        ALog.d(TAG, "load previous page ${(seriesAdapter?.firstPage ?: -88) - 1}")
                        seriesAdapter?.firstPage?.let {
                            if (it > 1) {
                                viewModel.getAllSeries(it - 1)
                                showLoadingOverlay()
                            }
                        }
                    }
                }
            })
            moviesRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        }

    }

    override fun setupObservers() {
        viewModel.series.observe(viewLifecycleOwner) {
            ALog.d(TAG, "getAllSeries: ${it.size}")
            seriesAdapter?.apply {
                setData(it, viewModel.imgDomain)
                ALog.d(TAG, "lastPosition: $lastPosition")
                binding.moviesRecycler.scrollToPosition(lastPosition)
            }
            binding.root.postDelayed({ hideLoadingOverlay() }, 1000)
        }
    }

    override fun initData() {
        if (viewModel.series.value == null) {
            viewModel.getAllSeries(0)
            showLoadingOverlay()
        }
    }

    private fun navigateToPlayer(item: Item) {
        ALog.i(TAG, "navigateToPlayer: $item")
        findNavController().navigate(NavGraphDirections.actionGlobalPlayerFragment(item = item.slug))
    }
}