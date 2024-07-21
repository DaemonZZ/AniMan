package com.daemonz.animange.ui.dialog

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.daemonz.animange.MainActivity
import com.daemonz.animange.R
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
class SearchDialog(private val onItemClickListener: OnItemClickListener<PagingData<Item>>) :
    BaseDialog() {
    companion object {
        private const val TAG = "SearchDialog"
    }

    private var _binding: SearchDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private var lastSearch: Long = 0L
    private var resultAdapter: SearchAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialogStyleNotTrans)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchDialogBinding.inflate(inflater, container, false)
        dialog?.window?.let {
            val param = it.attributes
            param.width = LayoutParams.MATCH_PARENT
            param.height = LayoutParams.MATCH_PARENT
            param.type = LayoutParams.TYPE_APPLICATION_PANEL
//            param.flags = LayoutParams.FLAG_LAYOUT_NO_LIMITS
            param.alpha = 1f
            it.attributes = param
            it.clearFlags(LayoutParams.FLAG_DIM_BEHIND)
            it.attributes.windowAnimations = R.style.FullScreenDialogStyleNotTrans;
        }
        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchView.editText.doOnTextChanged { text, _, _, _ ->
            if (binding.searchView.editText.text.toString()
                    .makeSearchText() != binding.searchView.editText.text.toString()
            ) {
                binding.searchView.editText.setText(
                    binding.searchView.editText.text.toString().makeSearchText()
                )
                binding.searchView.editText.setSelection(binding.searchView.editText.length())
                return@doOnTextChanged
            }
            lastSearch = SystemClock.elapsedRealtime()
            binding.searchView.postDelayed({
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
        binding.searchBar.setOnMenuItemClickListener {
            if (it.itemId == R.id.close) {
                dismiss()
                true
            } else false
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            resultAdapter = SearchAdapter(onItemClickListener, currentTheme)
            resultRecycler.adapter = resultAdapter
            resultRecycler.addOnScrollListener(object : OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (!recyclerView.canScrollVertically(1)) {
                        ALog.d(TAG, "load new page ${(resultAdapter?.lastPage ?: -88) + 1}")
                        resultAdapter?.lastPage?.let {
                            viewModel.search(searchView.editText.text.toString(), it + 1)
                        }
                    }
                    if (!recyclerView.canScrollVertically(-1)) {
                        ALog.d(TAG, "load previous page ${(resultAdapter?.firstPage ?: -88) - 1}")
                        resultAdapter?.firstPage?.let {
                            if (it > 1) {
                                viewModel.search(searchView.editText.text.toString(), it - 1)
                            }

                        }
                    }
                }
            })
        }
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}