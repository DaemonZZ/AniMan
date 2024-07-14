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
import com.daemonz.animange.viewmodel.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoviesFragment :
    BaseFragment<FragmentGridListBinding, MoviesViewModel>(FragmentGridListBinding::inflate),
    BottomNavigationAction {
    override val viewModel: MoviesViewModel by viewModels()
    private val onItemClickListener =
        OnItemClickListener<PagingData<Item>> { item, index ->
            ALog.i(TAG, "onItemClick: $index, status: ${item.data.status}")
            navigateToPlayer(item.data)
        }

    private var moviesAdapter: GridAdapter? = null

    override fun setupViews() {
        binding.apply {
            moviesRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
            moviesAdapter = GridAdapter(onItemClickListener)
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
                            showLoadingOverlay("getMovies")
                        }
                    }
                    if (!recyclerView.canScrollVertically(-1)) {
                        ALog.d(TAG, "load previous page ${(moviesAdapter?.firstPage ?: -88) - 1}")
                        moviesAdapter?.firstPage?.let {
                            if (it > 1) {
                                viewModel.getListMovies(it - 1)
                                showLoadingOverlay("getMovies")
                            }

                        }
                    }
                }
            })
        }

    }

    override fun setupObservers() {
        viewModel.movies.observe(viewLifecycleOwner) {
            ALog.d(TAG, "movies: ${it.size}")
            moviesAdapter?.apply {
                setData(it, viewModel.imgDomain)
                ALog.d(TAG, "lastPosition: $lastPosition")
                binding.moviesRecycler.scrollToPosition(lastPosition)
            }
            binding.root.postDelayed({ hideLoadingOverlay("getMovies") }, 1000)

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
        if (viewModel.movies.value == null) {
            viewModel.getListMovies()
            showLoadingOverlay("getMovies")
        }
    }

    private fun navigateToPlayer(item: Item) {
        ALog.i(TAG, "navigateToPlayer: $item")
        findNavController().navigate(NavGraphDirections.actionGlobalPlayerFragment(item = item.slug))
    }
}