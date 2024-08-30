package com.daemonz.animange.fragment

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.daemonz.animange.MainActivity
import com.daemonz.animange.NavGraphDirections
import com.daemonz.animange.R
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
        (activity as? MainActivity)?.setTitle(getString(R.string.favourite))
        binding.apply {
            recycler.layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = FavouriteAdapter(
                onFavourite = { item ->
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
                    findNavController().navigate(NavGraphDirections.actionGlobalPlayerFragment(item = item.slug))
                },
                theme = currentTheme
            )
            recycler.adapter = adapter
        }
    }

    override fun syncTheme() {
        super.syncTheme()
        adapter?.syncTheme(currentTheme)
        viewModel.favorite.value.let {
            if (it.isNullOrEmpty()) {
                ALog.d(TAG, "syncTheme : ${binding.textNoResult.text}")
                binding.textNoResult.visibility = View.VISIBLE
                binding.textNoResult.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
                binding.recycler.visibility = View.GONE
            } else {
                binding.textNoResult.visibility = View.GONE
                binding.recycler.visibility = View.VISIBLE
            }
        }
    }
    override fun setupObservers() {
        viewModel.favorite.observe(viewLifecycleOwner) {
            ALog.d(TAG, "favorite: ${it.size}")
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

    override fun initData() {
        viewModel.getFavourite()
    }
}