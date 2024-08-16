package com.daemonz.animange.fragment

import android.content.res.ColorStateList
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.daemonz.animange.MainActivity
import com.daemonz.animange.NavGraphDirections
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.SearchDialogBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.entity.toItem
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.SearchAdapter
import com.daemonz.animange.util.SEARCH_TIME_DELAY
import com.daemonz.animange.util.makeSearchText
import com.daemonz.animange.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment :
    BaseFragment<SearchDialogBinding, SearchViewModel>(SearchDialogBinding::inflate) {
    override val viewModel: SearchViewModel by viewModels()
    private var lastSearch: Long = 0L
    private var resultAdapter: SearchAdapter? = null
    private val onItemClickListener =
        OnItemClickListener<PagingData<Item>> { item, index ->
            ALog.i(TAG, "onItemClick: $index, status: ${item.data.status}")
            navigateToPlayer(item.data)
        }

    private fun navigateToPlayer(data: Item) {
        ALog.i(TAG, "navigateToPlayer: $data")
        viewModel.saveSearchHistory(data)
        findNavController().navigate(NavGraphDirections.actionGlobalPlayerFragment(item = data.slug))
    }

    override fun setupViews() {
        binding.edtSearch.setOnEditorActionListener { v, actionId, event ->
            if (binding.edtSearch.text.toString()
                    .makeSearchText() != binding.edtSearch.text.toString()
            ) {
                binding.edtSearch.setText(
                    binding.edtSearch.text.toString().makeSearchText()
                )
                binding.edtSearch.setSelection(binding.edtSearch.length())
                return@setOnEditorActionListener true
            }
            lastSearch = SystemClock.elapsedRealtime()
            binding.searchLayout.postDelayed({
                if (SystemClock.elapsedRealtime() - lastSearch > SEARCH_TIME_DELAY && v.text.toString().length > 3) {
                    viewModel.clearCache()
                    viewModel.search(v.text.toString().trim())
                    (activity as? MainActivity)?.showLoadingOverlay(parentFragmentManager)
                }
            }, 2000L)
            if (v.text.toString().isEmpty()) {
                resultAdapter?.setData(listOf())
            }
            true
        }
        binding.apply {
            ALog.d(TAG, "bindViewabc: $currentTheme")
            resultAdapter = SearchAdapter(onItemClickListener, currentTheme)
            resultRecycler.adapter = resultAdapter
            resultRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
            resultRecycler.addOnScrollListener(object : OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (!recyclerView.canScrollVertically(1)) {
                        ALog.d(TAG, "load new page ${(resultAdapter?.lastPage ?: -88) + 1}")
                        resultAdapter?.lastPage?.let {
                            viewModel.search(edtSearch.text.toString(), it + 1)
                        }
                    }
                    if (!recyclerView.canScrollVertically(-1)) {
                        ALog.d(TAG, "load previous page ${(resultAdapter?.firstPage ?: -88) - 1}")
                        resultAdapter?.firstPage?.let {
                            if (it > 1) {
                                viewModel.search(edtSearch.text.toString(), it - 1)
                            }

                        }
                    }
                }
            })
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun setupObservers() {
        viewModel.searchResult.observe(viewLifecycleOwner) {
            ALog.d(TAG, "searchResult: ${it.size}")
            resultAdapter?.setData(it, viewModel.imgDomain)
            (activity as? MainActivity)?.hideLoadingOverlay()
            binding.apply {
                textNoResult.isVisible = it.isEmpty()
                resultRecycler.isVisible = it.isNotEmpty()
            }
        }
        viewModel.searchHistoryData.observe(viewLifecycleOwner) {
            ALog.d(TAG, "searchHistoryData: ${it.size}")
            binding.apply {
                if (it.isEmpty()) {
                    textNoResult.isVisible = true
                    resultRecycler.isVisible = false
                    lbHistory.isVisible = false
                } else {
                    textNoResult.isVisible = false
                    resultRecycler.isVisible = true
                    lbHistory.isVisible = true
                    resultAdapter?.setData(it.map {
                        PagingData(0, it.toItem())
                    }, "")
                }
            }

        }
    }

    override fun syncTheme() {
        super.syncTheme()
        binding.apply {
            setupViews()
            btnFilter.setImageResource(currentTheme.iconFilter())
            btnBack.setImageResource(currentTheme.iconBack())
            searchLayout.hintTextColor =
                ColorStateList.valueOf(currentTheme.firstActivityTextColor(requireContext()))
            searchLayout.boxStrokeColor = currentTheme.firstActivityTextColor(requireContext())
            edtSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    requireContext(),
                    currentTheme.iconSearch()
                ), null, null, null
            )
        }
    }

    override fun initData() {
        viewModel.getSearchHistory()
    }
}