package com.daemonz.animange.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.PlayerViewFragmentBinding
import com.daemonz.animange.entity.ListData
import com.daemonz.animange.fragment.player.ChildPlayerFragmentActions
import com.daemonz.animange.fragment.player.EpisodesFragment
import com.daemonz.animange.fragment.player.SuggestionFragment
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.PlayerPagerAdapter
import com.daemonz.animange.ui.dialog.PlayerMaskDialog
import com.daemonz.animange.ui.view_helper.CustomWebClient
import com.daemonz.animange.util.AppUtils
import com.daemonz.animange.util.ITEM_STATUS_TRAILER
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.PLAYER_DEEP_LINK
import com.daemonz.animange.viewmodel.PlayerViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerFragment :
    BaseFragment<PlayerViewFragmentBinding, PlayerViewModel>(PlayerViewFragmentBinding::inflate) {
    override val viewModel: PlayerViewModel by viewModels()
    private val arg: PlayerFragmentArgs by navArgs()

    private var lastTouchWebView = 0L

    private val listFrag = listOf<Fragment>(
        SuggestionFragment(),
        EpisodesFragment()
    )

    private var pagerAdapter: PlayerPagerAdapter? = null

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val b = super.onCreateView(inflater, container, savedInstanceState)
        binding.apply {
            activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
                findNavController().popBackStack()
            }
            videoView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?, request: WebResourceRequest?
                ): Boolean {
                    viewModel.currentPlaying.value?.getCurrentEpisodeDetail()?.url?.let {
                        view?.loadUrl(
                            it
                        )
                    }
                    return false
                }
            }
            videoView.setOnTouchListener { v, event ->
                if (SystemClock.elapsedRealtime() - lastTouchWebView > 1000) {
                    ALog.d(TAG, "click on webview + ${event.action}")
                    toggleToolBarShowing(
                        isShow = true, autoHide = true
                    )
                }
                lastTouchWebView = SystemClock.elapsedRealtime()
                v.onTouchEvent(event)
                true
            }
            videoView.settings.apply {
                javaScriptEnabled = true
                useWideViewPort = false
            }
            videoView.webChromeClient = CustomWebClient(showWebView = {
                videoView.visibility = View.VISIBLE
            }, hideWebView = { fullscreen ->
                videoView.visibility = View.GONE
                if (fullscreen != null) {
                    (requireActivity().window.decorView as? FrameLayout)?.removeView(fullscreen)
                }
                activity?.window?.decorView?.apply {
                    systemUiVisibility =
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
                }
                fullscreen?.setOnTouchListener { v, _ ->
                    v.postDelayed({
                        activity?.window?.decorView?.apply {
                            systemUiVisibility =
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
                        }
                    }, 3000L)
                    true
                }

            }, addView = { fullscreen ->
                val param = FrameLayout.LayoutParams(-1, -1)
                (requireActivity().window.decorView as? FrameLayout)?.addView(fullscreen, param)
                val dialog = PlayerMaskDialog()
                dialog.show(childFragmentManager, "PlayerMaskDialog")
            })
        }
        return b
    }

    override fun initData() {
        viewModel.loadData(arg.item)
        showLoadingOverlay("loadData")
    }

    override fun setupViews() {
        binding.apply {
                textTitle.setOnClickListener {
                    if (textDesc.visibility == View.VISIBLE) {
                        expandView(false)
                    } else {
                        expandView(true)
                    }
                }


            btnFollow.setOnClickListener {
                if (LoginData.account == null) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.user_not_logged_in),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (viewModel.isFavourite.value == true) {
                        viewModel.unMarkItemAsFavorite()
                    } else {
                        viewModel.markItemAsFavorite()
                    }
                }
            }
            btnShare.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "$PLAYER_DEEP_LINK${arg.item}")
                    type = "text/html"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
            binding.btnFollow.isChecked = true
            listFrag.forEach {
                (it as? ChildPlayerFragmentActions)?.setupViewModel(viewModel)
            }
            pagerAdapter = PlayerPagerAdapter(listFrag, this@PlayerFragment)
            viewPager.adapter = pagerAdapter
            TabLayoutMediator(tabSuggest, viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.suggest)
                    1 -> tab.text = getString(R.string.episodes)
                }
            }.attach()
        }
    }

    private fun expandView(expand: Boolean) {
        binding.apply {
            if (expand) {
                textTitle.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.close, 0
                )
                textYear.visibility = View.VISIBLE
                textCountry.visibility = View.VISIBLE
                textCategory.visibility = View.VISIBLE
                textDuration.visibility = View.VISIBLE
                textEpisodes.visibility = View.VISIBLE
                textDesc.visibility = View.VISIBLE
                textOriginName.visibility = View.VISIBLE
            } else {
                textTitle.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.keyboard_arrow_down, 0
                )
                textYear.visibility = View.GONE
                textCountry.visibility = View.GONE
                textCategory.visibility = View.GONE
                textDuration.visibility = View.GONE
                textEpisodes.visibility = View.GONE
                textDesc.visibility = View.GONE
                textOriginName.visibility = View.GONE
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
        viewModel.apply {
            playerData.observe(viewLifecycleOwner) {
                ALog.d(TAG, "playerData: ${it.data.seoOnPage}")
                if (it.data.item?.status == ITEM_STATUS_TRAILER) {
                    findNavController().popBackStack()
                    AppUtils.playYoutube(
                        requireContext(), it.data.item.trailerUrl
                    )
                } else {
                    loadPlayerData(it)
                    getSuggestions()
                    showLoadingOverlay("getSuggestions")
                }
                hideLoadingOverlay("loadData")

            }
            currentPlaying.observe(viewLifecycleOwner) {
                ALog.d(TAG, "currentPlaying: ${it.pivot}")
                binding.videoView.loadUrl(it.getCurrentEpisodeDetail().url)
                binding.textTitle.text = requireContext().getString(
                    R.string.player_title,
                    viewModel.playerData.value?.data?.item?.name,
                    (it.pivot + 1).toString()
                )
            }
            isFavourite.observe(viewLifecycleOwner) {
                if (it) {
                    binding.btnFollow.setCompoundDrawablesWithIntrinsicBounds(
                        0, R.drawable.favorite_filled, 0, 0
                    )
                } else {
                    binding.btnFollow.setCompoundDrawablesWithIntrinsicBounds(
                        0, R.drawable.favorite, 0, 0
                    )
                }
            }
        }
    }

    private fun loadPlayerData(data: ListData) {
        binding.apply {
            textDesc.text = Html.fromHtml(data.data.item?.content, Html.FROM_HTML_MODE_LEGACY)
            textYear.text = requireContext().getString(R.string.created_year, data.data.item?.year)
            textCategory.text = requireContext().getString(
                R.string.category,
                data.data.item?.category?.joinToString { it.name })
            textDuration.text = requireContext().getString(R.string.duration, data.data.item?.time)
            textEpisodes.text = requireContext().getString(
                R.string.num_of_episode,
                data.data.item?.episodeTotal,
            )
            textOriginName.text =
                requireContext().getString(R.string.original_name, data.data.item?.originName)
            textCountry.text = requireContext().getString(
                R.string.country,
                data.data.item?.country?.joinToString { it.name })

        }
    }

    fun setFilmId(slug: String) {
        ALog.d(TAG, "setFilmId: $slug -- ${viewModel.playerData.value?.data?.item?.slug}")
        if (viewModel.playerData.value?.data?.item?.slug == slug) return
        binding.root.post {
            viewModel.loadData(slug)
        }
    }
}