package com.daemonz.animange.fragment

import android.view.View
import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentFavouritesBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.FavouriteAdapter
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.viewmodel.FavouriteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouritesFragment :
    BaseFragment<FragmentFavouritesBinding, FavouriteViewModel>(FragmentFavouritesBinding::inflate) {
    override val viewModel: FavouriteViewModel by viewModels()
    private var adapter: FavouriteAdapter? = null

    override fun setupViews() {
        binding.apply {
            adapter = FavouriteAdapter(onFavourite = { item ->
                LoginData.getActiveUser()?.let {
                    ALog.d(TAG, "onFavourite: ${it.isFavourite(item.slug)}")
                    if (it.isFavourite(item.slug)) {
                        viewModel.unMarkItemAsFavorite(item)
                    } else {
                        viewModel.markItemAsFavorite(item)
                    }
                }
            },
                onItemClickListener = { item, _ ->
                    ALog.d(TAG, "onItemClick: ${item.slug}")
                    showPlayer(
                        item.slug
                    )
                })
            recycler.adapter = adapter
            LoginData.getActiveUser()?.favorites?.let {
                if (it.isEmpty()) {
                    binding.textNoResult.visibility = View.VISIBLE
                    binding.recycler.visibility = View.GONE
                } else {
                    binding.textNoResult.visibility = View.GONE
                    binding.recycler.visibility = View.VISIBLE
                    adapter?.setData(it)
                }
            }
        }
    }

    override fun setupObservers() {

    }
}