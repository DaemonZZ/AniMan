package com.daemonz.animange.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.addCallback
import androidx.fragment.app.DialogFragment.STYLE_NO_TITLE
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.PlayerViewFragmentBinding
import com.daemonz.animange.databinding.TransparentLayoutBinding
import com.daemonz.animange.ui.dialog.PlayerMaskDialog
import com.daemonz.animange.ui.view_helper.CustomWebClient
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
            activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
                findNavController().popBackStack()
            }
            videoView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    view?.loadUrl("https://vip.opstream17.com/share/8617f303dd11780c5d48aedf0bd90823")
                    return false
                }
            }
            videoView.settings.apply {
                javaScriptEnabled = true
                useWideViewPort = false
            }
            videoView.webChromeClient = CustomWebClient(
                showWebView = {
                    videoView.visibility = View.VISIBLE
                },
                hideWebView = { fullscreen ->
                    videoView.visibility = View.GONE
                    if(fullscreen!= null) {
                        (requireActivity().window.decorView as? FrameLayout)?.removeView(fullscreen)
                    }
                },
                addView = {fullscreen->
                    val param = FrameLayout.LayoutParams(-1,-1)
                    (requireActivity().window.decorView as? FrameLayout)?.addView(fullscreen,param)
                    val dialog = PlayerMaskDialog()
                    dialog.show(childFragmentManager, "PlayerMaskDialog")
                }
            )
            if(savedInstanceState == null) {
                videoView.loadUrl("https://vip.opstream17.com/share/8617f303dd11780c5d48aedf0bd90823")
            }

        }
        return b
    }


    override fun setupViews() {
        binding.apply {
            btn.setOnClickListener {
                val dialog = PlayerMaskDialog()
                dialog.show(childFragmentManager, "PlayerMaskDialog")
            }
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