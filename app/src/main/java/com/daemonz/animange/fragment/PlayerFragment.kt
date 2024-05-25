package com.daemonz.animange.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.PlayerViewFragmentBinding
import com.daemonz.animange.viewmodel.HomeViewModel

class PlayerFragment: BaseFragment<PlayerViewFragmentBinding, HomeViewModel>(PlayerViewFragmentBinding::inflate) {
    override val viewModel: HomeViewModel by viewModels()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val b = super.onCreateView(inflater, container, savedInstanceState)
        binding.apply {
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
            if(savedInstanceState == null) {
                videoView.loadUrl("https://vip.opstream17.com/share/8617f303dd11780c5d48aedf0bd90823")
            }

        }
        return b
    }


    override fun setupViews() {

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