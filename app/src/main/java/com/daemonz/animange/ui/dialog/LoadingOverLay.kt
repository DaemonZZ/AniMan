package com.daemonz.animange.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.daemonz.animange.R
import com.daemonz.animange.databinding.LoadingOverlayBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.loadGif
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingOverLay : DialogFragment() {
    companion object {
        private const val TAG = "LoadingOverLay"
    }
    private var _binding: LoadingOverlayBinding? = null
    private val binding get() = _binding!!
    private var countdown = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoadingOverlayBinding.inflate(inflater, container, false)
        binding.img.loadGif(R.drawable.loading_288px)
        return binding.root
    }

    override fun show(manager: FragmentManager, tag: String?) {
        lifecycleScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
            ALog.e(TAG, "show: ${throwable.message}")
            dismiss()
        }) {
            try {
                if (countdown >= 0) {
                    if (!isAdded) {
                        super.show(manager, tag)
                    }
                    countdown = 5000
                    while (countdown > 0) {
                        countdown -= 50
                        delay(50)
                    }
                    dismiss()
                } else {
                    countdown = 5000
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun hide() {
        countdown = 0
    }
}