package com.daemonz.animange.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.daemonz.animange.R
import com.daemonz.animange.databinding.LoadingOverlayBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.thememanager.AnimanTheme
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingOverLay(theme: AnimanTheme) : BaseDialog(theme) {
    companion object {
        private const val TAG = "LoadingOverLay"
    }
    private var _binding: LoadingOverlayBinding? = null
    private val binding get() = _binding!!
    private var countdown = 0
    private var animation: Animation? = null
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.img.setImageResource(currentTheme.loadingIcon())
        binding.textLoading.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
        animation = AnimationUtils.loadAnimation(requireContext(), R.anim.loop_rotate)
        animation?.interpolator = LinearInterpolator()
        animation?.repeatCount = Animation.INFINITE
        animation?.repeatMode = Animation.RESTART
        binding.img.startAnimation(animation)
        dialog?.window?.setDimAmount(0.4f)
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