package com.daemonz.animange.fragment

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.daemonz.animange.MainActivity
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentTab1Binding
import com.daemonz.animange.databinding.FragmentTab5Binding
import com.daemonz.animange.entity.FavouriteItem
import com.daemonz.animange.entity.Item
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.BottomNavigationAction
import com.daemonz.animange.ui.CommonAction
import com.daemonz.animange.ui.adapter.FavouriteAdapter
import com.daemonz.animange.ui.adapter.GridAdapter
import com.daemonz.animange.ui.dialog.SearchDialog
import com.daemonz.animange.viewmodel.HomeViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database

class Tab5Fragment : BaseFragment<FragmentTab5Binding, HomeViewModel>(FragmentTab5Binding::inflate),
    BottomNavigationAction {
    override val viewModel: HomeViewModel by activityViewModels()
    private val onItemClickListener = object : OnItemClickListener<FavouriteItem> {
        override fun onItemClick(item: FavouriteItem, index: Int) {
            navigateToPlayer(item.slug)
        }
    }
    private var favouriteAdapter: FavouriteAdapter? = null

    override fun setupViews() {
        binding.apply {
            favoriteRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
            favouriteAdapter = FavouriteAdapter(onItemClickListener)
            favoriteRecycler.adapter = favouriteAdapter
            favoriteRecycler.addOnScrollListener(object : OnScrollListener() {
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
        viewModel.favourites.observe(viewLifecycleOwner) {
            favouriteAdapter?.setData(it)
            hideLoadingOverlay("getFavourites")
        }
    }

    override fun onSearch() {
        SearchDialog(object : OnItemClickListener<Item> {
            override fun onItemClick(item: Item, index: Int) {
                navigateToPlayer(item.slug)
            }
        }).show(childFragmentManager, "SearchDialog")
    }

    override fun onRefresh() {
        TODO("Not yet implemented")
    }

    override fun onReSelectBottomNavigationItem(itemId: Int) {
        TODO("Not yet implemented")
    }

    override fun initData() {
        viewModel.getFavourites()
        showLoadingOverlay("getFavourites")
    }

    private fun navigateToPlayer(slug: String) {
        ALog.i(TAG, "navigateToPlayer: $slug")
        findNavController().navigate(Tab5FragmentDirections.actionTab5FragmentToPlayerFragment(item = slug))
    }
}