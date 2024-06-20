package com.daemonz.animange.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.daemonz.animange.MainActivity
import com.daemonz.animange.log.ALog

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding, VM : ViewModel>(
    private val inflate: Inflate<VB>
) : Fragment() {
    protected val TAG: String = this::class.java.simpleName
    private var _binding: VB? = null
    protected val binding: VB by lazy { _binding!! }
    protected abstract val viewModel: VM


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
//            OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                // disable swipe back gesture
//            }
//        })
        setupViews()
        setupObservers()
        initData()
    }

    fun showToastNotImplemented() {
        Toast.makeText(
            requireContext(),
            "Tính năng sẽ được phát triển trong tương lai",
            Toast.LENGTH_SHORT
        ).show()
    }


    open fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showLoadingOverlay(id: String) {
        ALog.d(TAG, "showLoadingOverlay $id")
        (activity as? MainActivity)?.showLoadingOverlay(parentFragmentManager, id)
    }

    fun hideLoadingOverlay(id: String) {
        (activity as? MainActivity)?.hideLoadingOverlay(id)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    abstract fun setupViews()


    abstract fun setupObservers()
    open fun initData() {
        //empty
    }
    fun toggleToolBarShowing(isShow: Boolean? = null, autoHide: Boolean = false) {
        (activity as? MainActivity)?.toggleToolBarShowing(isShow, autoHide)
    }
}
