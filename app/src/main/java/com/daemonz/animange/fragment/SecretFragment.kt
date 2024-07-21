package com.daemonz.animange.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.daemonz.animange.NavGraphDirections
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.FragmentSecretBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.BottomNavigationAction
import com.daemonz.animange.ui.adapter.GridAdapter
import com.daemonz.animange.ui.dialog.SearchDialog
import com.daemonz.animange.viewmodel.SecretViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecretFragment :
    BaseFragment<FragmentSecretBinding, SecretViewModel>(FragmentSecretBinding::inflate),
    BottomNavigationAction {
    override val viewModel: SecretViewModel by viewModels()
    private val onItemClickListener =
        OnItemClickListener<PagingData<Item>> { item, index ->
            ALog.i(TAG, "onItemClick: $index, status: ${item.data.status}")
            navigateToPlayer(item.data)
        }

    private var adapter: GridAdapter? = null

    override fun setupViews() {
        binding.apply {
            recycler.layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = GridAdapter(onItemClickListener, currentTheme)
            recycler.adapter = adapter
            recycler.addOnScrollListener(object : OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    ALog.i(TAG, "onScrollStateChanged: state: $newState")
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        toggleToolBarShowing(
                            isShow = true,
                            autoHide = true
                        )
                    }
                    if (!recyclerView.canScrollVertically(1)) {
                        ALog.d(TAG, "load new page ${(adapter?.lastPage ?: -88) + 1}")
                        adapter?.lastPage?.let {
                            viewModel.getSecret(it + 1)
                            showLoadingOverlay()
                        }
                    }
                    if (!recyclerView.canScrollVertically(-1)) {
                        ALog.d(TAG, "load previous page ${(adapter?.firstPage ?: -88) - 1}")
                        adapter?.firstPage?.let {
                            if (it > 1) {
                                viewModel.getSecret(it - 1)
                                showLoadingOverlay()
                            }
                        }
                    }
                }
            })
        }

    }

    override fun setupObservers() {
        viewModel.secret.observe(viewLifecycleOwner) {
            ALog.d(TAG, "getSecret: ${it.size}")
            adapter?.apply {
                setData(it, viewModel.imgDomain)
                ALog.d(TAG, "lastPosition: $lastPosition")
                binding.recycler.scrollToPosition(lastPosition)
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
        viewModel.getSecret(0)
        showLoadingOverlay()
    }

    private fun navigateToPlayer(item: Item) {
        ALog.i(TAG, "navigateToPlayer: $item")
        findNavController().navigate(NavGraphDirections.actionGlobalPlayerFragment(item = item.slug))
    }
}