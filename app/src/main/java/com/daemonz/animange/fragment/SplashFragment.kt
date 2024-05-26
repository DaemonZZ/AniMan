package com.daemonz.animange.fragment

import android.app.Service
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentSplashBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.dialog.PlayerMaskDialog
import com.daemonz.animange.viewmodel.SplashViewModel

class SplashFragment: BaseFragment<FragmentSplashBinding, SplashViewModel>(FragmentSplashBinding::inflate) {
    override val viewModel: SplashViewModel by viewModels<SplashViewModel>()

    override fun setupViews() {
        binding.apply {
            btn.setOnClickListener {
                ALog.d(TAG, "btn click")
                findNavController().navigate(SplashFragmentDirections.toHomeFragment())
//                inflaterView()
//                val dialog = PlayerMaskDialog()
//                dialog.show(childFragmentManager, "PlayerMaskDialog")
            }
        }
    }
//    fun inflaterView() {
//        val inflater = requireContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val window = inflater.inflate(R.layout.transparent_layout, null)
//        val winParams = WindowManager.LayoutParams(
//            WindowManager.LayoutParams.WRAP_CONTENT,
//            WindowManager.LayoutParams.WRAP_CONTENT,
//            WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
//            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
//            PixelFormat.TRANSLUCENT
//        )
//        winParams.gravity = Gravity.BOTTOM or Gravity.END
//        val windowManager = requireContext().getSystemService(Service.WINDOW_SERVICE) as WindowManager
//        windowManager.addView(window, winParams)
//        window.findViewById<Button>(R.id.btn).setOnClickListener {
//            ALog.d(TAG, "btn trans click")
//        }
//    }
    override fun setupObservers() {

    }

}