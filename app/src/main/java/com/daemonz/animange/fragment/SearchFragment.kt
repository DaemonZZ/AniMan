package com.daemonz.animange.fragment

import android.content.res.ColorStateList
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
        findNavController().navigate(NavGraphDirections.actionGlobalPlayerFragment(item = data.slug))
    }

    override fun setupViews() {
        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            if (binding.edtSearch.text.toString()
                    .makeSearchText() != binding.edtSearch.text.toString()
            ) {
                binding.edtSearch.setText(
                    binding.edtSearch.text.toString().makeSearchText()
                )
                binding.edtSearch.setSelection(binding.edtSearch.length())
                return@doOnTextChanged
            }
            lastSearch = SystemClock.elapsedRealtime()
            binding.searchLayout.postDelayed({
                if (SystemClock.elapsedRealtime() - lastSearch > SEARCH_TIME_DELAY && text.toString().length > 3) {
                    viewModel.clearCache()
                    viewModel.search(text.toString().trim())
                    (activity as? MainActivity)?.showLoadingOverlay(parentFragmentManager)
                }
            }, 2000L)
            if (text.toString().isEmpty()) {
                resultAdapter?.setData(listOf())
            }
        }
        binding.apply {
            resultAdapter = SearchAdapter(onItemClickListener, currentTheme)
            resultRecycler.adapter = resultAdapter
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
    }

    override fun syncTheme() {
        super.syncTheme()
        binding.apply {
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
}