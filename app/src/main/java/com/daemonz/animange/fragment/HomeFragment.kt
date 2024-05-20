package com.daemonz.animange.fragment

import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentHomeBinding
import com.daemonz.animange.viewmodel.HomeViewModel

class HomeFragment: BaseFragment<FragmentHomeBinding,HomeViewModel>(FragmentHomeBinding::inflate) {
    override val mViewModel: HomeViewModel by viewModels<HomeViewModel>()

    override fun setupViews() {
        binding.apply {
//            videoView.setVideoPath("https://vip.opstream17.com/share/8617f303dd11780c5d48aedf0bd90823")
            val uri = Uri.parse("https://media.geeksforgeeks.org/wp-content/uploads/20201217192146/Screenrecorder-2020-12-17-19-17-36-828.mp4?_=1")
            videoView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    view?.loadUrl("https://vip.opstream17.com/share/8617f303dd11780c5d48aedf0bd90823")
                    return false
                }
            }
            videoView.settings.javaScriptEnabled = true
          videoView.loadUrl("https://vip.opstream17.com/share/8617f303dd11780c5d48aedf0bd90823")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.videoView.saveState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            binding.videoView.restoreState(savedInstanceState)
        }
    }

    override fun setupObservers() {
    }
}