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
import com.daemonz.animange.ui.BottomNavigationAction
import com.daemonz.animange.ui.adapter.GridAdapter
import com.daemonz.animange.ui.dialog.SearchDialog
import com.daemonz.animange.viewmodel.TvShowViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TvShowFragment :
    BaseFragment<FragmentGridListBinding, TvShowViewModel>(FragmentGridListBinding::inflate),
    BottomNavigationAction {
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
                    if (!recyclerView.canScrollVertically(-1)) {
                        ALog.d(TAG, "load previous page ${(tvAdapter?.firstPage ?: -88) - 1}")
                        tvAdapter?.firstPage?.let {
                            if (it > 1) {
                                viewModel.getTvShows(it - 1)
                                showLoadingOverlay()
                            }
                        }
                    }
                }
            })
        }

    }

    override fun setupObservers() {
        viewModel.shows.observe(viewLifecycleOwner) {
            ALog.d(TAG, "getTvShows: ${it.size}")
            tvAdapter?.apply {
                setData(it, viewModel.imgDomain)
                ALog.d(TAG, "lastPosition: $lastPosition")
                binding.moviesRecycler.scrollToPosition(lastPosition)
            }
            binding.root.postDelayed({ hideLoadingOverlay() }, 1000)
        }
    }

    override fun onSearch() {
        SearchDialog(onItemClickListener).show(childFragmentManager, "SearchDialog")
    }

    override fun onRefresh() {
        TODO("Not yet implemented")
    }

    override fun onReSelectBottomNavigationItem(itemId: Int) {
        TODO("Not yet implemented")
    }

    override fun initData() {
        viewModel.getTvShows(1)
        showLoadingOverlay()
    }

    private fun navigateToPlayer(item: Item) {
        ALog.i(TAG, "navigateToPlayer: $item")
        findNavController().navigate(NavGraphDirections.actionGlobalPlayerFragment(item = item.slug))
    }
}