package com.daemonz.animange.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.daemonz.animange.MainActivity
import com.daemonz.animange.R
import com.daemonz.animange.log.ALog

abstract class BaseDialogFragment<VB : ViewBinding, VM : ViewModel>(
    private val inflate: Inflate<VB>
) : DialogFragment() {
    val TAG: String = this::class.java.simpleName
    var _binding: VB? = null
    protected val binding: VB by lazy { _binding!! }
    protected abstract val viewModel: VM


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialogStyleNotTrans)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        _binding = inflate.invoke(inflater, container, false)
        dialog?.window?.let {
            val param = it.attributes
            param.width = LayoutParams.MATCH_PARENT
            param.height = LayoutParams.MATCH_PARENT
            param.type = LayoutParams.TYPE_APPLICATION_PANEL
//            param.flags = LayoutParams.FLAG_LAYOUT_NO_LIMITS
            param.alpha = 1f
            it.attributes = param
            it.clearFlags(LayoutParams.FLAG_DIM_BEHIND)
            it.attributes.windowAnimations = R.style.FullScreenDialogStyleNotTrans
        }
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