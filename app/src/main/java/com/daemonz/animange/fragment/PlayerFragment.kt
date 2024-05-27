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
import androidx.navigation.fragment.navArgs
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.PlayerViewFragmentBinding
import com.daemonz.animange.databinding.TransparentLayoutBinding
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.dialog.PlayerMaskDialog
import com.daemonz.animange.ui.view_helper.CustomWebClient
import com.daemonz.animange.viewmodel.HomeViewModel
import com.daemonz.animange.viewmodel.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerFragment :
    BaseFragment<PlayerViewFragmentBinding, PlayerViewModel>(PlayerViewFragmentBinding::inflate) {
    override val viewModel: PlayerViewModel by viewModels()
    private val arg: PlayerFragmentArgs by navArgs()

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
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
                    viewModel.currentPlaying.value?.getCurrentEpisodeDetail()?.url?.let {
                        view?.loadUrl(
                            it
                        )
                    }
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
                    if (fullscreen != null) {
                        (requireActivity().window.decorView as? FrameLayout)?.removeView(fullscreen)
                    }
                    activity?.window?.decorView?.apply {
                        systemUiVisibility =
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
                    }
                    fullscreen?.setOnTouchListener { v, event ->
                        v.postDelayed({
                            activity?.window?.decorView?.apply {
                                systemUiVisibility =
                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
                            }
                        }, 3000L)
                        true
                    }

                },
                addView = { fullscreen ->
                    val param = FrameLayout.LayoutParams(-1, -1)
                    (requireActivity().window.decorView as? FrameLayout)?.addView(fullscreen, param)
                    val dialog = PlayerMaskDialog()
                    dialog.show(childFragmentManager, "PlayerMaskDialog")
                }
            )
//            if(savedInstanceState == null) {
//                videoView.loadUrl("https://vip.opstream17.com/share/8617f303dd11780c5d48aedf0bd90823")
//            }
        }
        return b
    }


    override fun setupViews() {
        binding.apply {
            btn.setOnClickListener {
                val dialog = PlayerMaskDialog()
                dialog.show(childFragmentManager, "PlayerMaskDialog")
            }
            viewModel.loadData(arg.item)
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
        viewModel.apply {
            playerData.observe(viewLifecycleOwner) {
                ALog.d(TAG, "playerData: $it")
            }
            currentPlaying.observe(viewLifecycleOwner) {
                ALog.d(TAG, "currentPlaying: $it")
                binding.videoView.loadUrl(it.getCurrentEpisodeDetail().url)
            }
        }
    }
}