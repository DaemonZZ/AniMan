package com.daemonz.animange.fragment

import android.content.res.ColorStateList
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.daemonz.animange.MainActivity
import com.daemonz.animange.NavGraphDirections
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.SearchDialogBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.entity.SearchHistory
import com.daemonz.animange.entity.toItem
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.SearchAdapter
import com.daemonz.animange.ui.adapter.SearchHistoryAdapter
import com.daemonz.animange.util.SEARCH_TIME_DELAY
import com.daemonz.animange.util.STRING_EMPTY
import com.daemonz.animange.util.makeSearchText
import com.daemonz.animange.util.setupClearButtonWithAction
import com.daemonz.animange.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment :
    BaseFragment<SearchDialogBinding, SearchViewModel>(SearchDialogBinding::inflate) {
    override val viewModel: SearchViewModel by hiltNavGraphViewModels(R.id.nav_search)
    private var resultAdapter: SearchAdapter? = null
    private var historyAdapter: SearchHistoryAdapter? = null
    private val onItemClickListener =
        OnItemClickListener<PagingData<Item>> { item, index ->
            ALog.i(TAG, "onItemClick: $index, status: ${item.data.status}")
            viewModel.saveSearchHistory(item.data)
            navigateToPlayer(item.data)
        }
    private val onItemHistoryClickListener =
        OnItemClickListener<Item> { item, index ->
            ALog.i(TAG, "onItemClick: $index, status: ${item.status}")
            navigateToPlayer(item)
        }

    private fun navigateToPlayer(data: Item) {
        ALog.i(TAG, "navigateToPlayer: $data")
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
            viewModel.clearCache()
            viewModel.search(v.text.toString().trim())
            (activity as? MainActivity)?.showLoadingOverlay(parentFragmentManager)
            if (v.text.toString().isEmpty()) {
                resultAdapter?.setData(listOf())
            }
            true
        }
        binding.apply {
            ALog.d(TAG, "bindView: $currentTheme")
            btnFilter.setOnClickListener {
                findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToSearchFilterFragment())
            }
            edtSearch.setText(STRING_EMPTY)
            edtSearch.clearFocus()
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

            historyAdapter = SearchHistoryAdapter(onItemHistoryClickListener, currentTheme)
            historyRecycler.adapter = historyAdapter
            historyRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    override fun setupObservers() {
        viewModel.searchResult.observe(viewLifecycleOwner) {
            resultAdapter?.setData(it, viewModel.imgDomain)
            (activity as? MainActivity)?.hideLoadingOverlay()
            binding.apply {
                textNoResult.isVisible = it.isEmpty()
                resultRecycler.isVisible = it.isNotEmpty()
                lbHistory.isVisible = false
                historyRecycler.isVisible = false

            }
        }
        viewModel.searchHistoryData.observe(viewLifecycleOwner) {
            updateHistoryData(it)
        }
    }

    private fun updateHistoryData(data: List<SearchHistory>) {
        binding.apply {
            resultRecycler.isVisible = false
            if (data.isEmpty()) {
                textNoResult.isVisible = true
                historyRecycler.isVisible = false
                lbHistory.isVisible = false
            } else {
                textNoResult.isVisible = false
                historyRecycler.isVisible = true
                lbHistory.isVisible = true
                historyAdapter?.setData(data.map { it.toItem() })
            }
        }
    }

    override fun syncTheme() {
        super.syncTheme()
        binding.apply {
            setupViews()
            binding.edtSearch.setupClearButtonWithAction(currentTheme) {
                viewModel.searchHistoryData.value?.let {
                    updateHistoryData(it)
                }
            }
            btnFilter.setImageResource(currentTheme.iconFilter())
            btnBack.setImageResource(currentTheme.iconBack())
            searchLayout.hintTextColor =
                ColorStateList.valueOf(currentTheme.firstActivityTextColor(requireContext()))
            searchLayout.boxStrokeColor = currentTheme.firstActivityTextColor(requireContext())
            textNoResult.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            lbHistory.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
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