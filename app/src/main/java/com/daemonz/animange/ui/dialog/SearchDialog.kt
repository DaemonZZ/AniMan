package com.daemonz.animange.ui.dialog

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.daemonz.animange.MainActivity
import com.daemonz.animange.R
import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.databinding.SearchDialogBinding
import com.daemonz.animange.entity.Item
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.SuggestionAdapter
import com.daemonz.animange.util.SEARCH_TIME_DELAY
import com.daemonz.animange.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchDialog(private val onItemClickListener: OnItemClickListener<Item>) : DialogFragment() {
    companion object {
        private const val TAG = "SearchDialog"
    }

    private var _binding: SearchDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private var lastSearch: Long = 0L
    private var resultAdapter: SuggestionAdapter? = null
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
            lastSearch = SystemClock.elapsedRealtime()
            binding.searchView.postDelayed({
                if (SystemClock.elapsedRealtime() - lastSearch > SEARCH_TIME_DELAY && text.toString().length > 3) {
                    ALog.d(TAG, "text: $text")
                    viewModel.search(text.toString())
                    (activity as? MainActivity)?.showLoadingOverlay(parentFragmentManager, "search")
                }
            }, 3000L)
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
            resultAdapter = SuggestionAdapter(onItemClickListener)
            resultRecycler.adapter = resultAdapter
        }
        viewModel.searchResult.observe(viewLifecycleOwner) {
            ALog.d(TAG, "searchResult: ${it.data.items.size}")
            resultAdapter?.setData(it.data.items, it.data.imgDomain)
            (activity as? MainActivity)?.hideLoadingOverlay("search")
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}