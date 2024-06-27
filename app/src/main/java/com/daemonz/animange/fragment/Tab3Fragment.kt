package com.daemonz.animange.fragment

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentTab3Binding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.BottomNavigationAction
import com.daemonz.animange.ui.adapter.GridAdapter
import com.daemonz.animange.ui.dialog.SearchDialog
import com.daemonz.animange.viewmodel.HomeViewModel

class Tab3Fragment : BaseFragment<FragmentTab3Binding, HomeViewModel>(FragmentTab3Binding::inflate),
    BottomNavigationAction {
    override val viewModel: HomeViewModel by activityViewModels()
    private val onItemClickListener = object : OnItemClickListener<Item> {
        override fun onItemClick(item: Item, index: Int) {
            ALog.i(TAG, "onItemClick: $index, status: ${item.status}")
            navigateToPlayer(item)
        }
    }

    private var seriesAdapter: GridAdapter? = null

    override fun setupViews() {
        binding.apply {
            moviesRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
            seriesAdapter = GridAdapter(onItemClickListener)
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
                }
            })
        }

    }

    override fun setupObservers() {
        viewModel.allSeries.observe(viewLifecycleOwner) {
            ALog.d(TAG, "getAllSeries: ${it.data.items.size}")
            seriesAdapter?.setData(it.data.items, it.data.imgDomain)
            hideLoadingOverlay("getAllSeries")
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
        showPlayer(item.slug)
    }
}