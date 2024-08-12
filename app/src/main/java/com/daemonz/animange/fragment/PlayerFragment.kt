package com.daemonz.animange.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daemonz.animange.MainActivity
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.PlayerViewFragmentBinding
import com.daemonz.animange.entity.ListData
import com.daemonz.animange.fragment.player.ChildPlayerFragmentActions
import com.daemonz.animange.fragment.player.CommentFragment
import com.daemonz.animange.fragment.player.EpisodesFragment
import com.daemonz.animange.fragment.player.RatingsFragment
import com.daemonz.animange.fragment.player.SuggestionFragment
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.adapter.PlayerPagerAdapter
import com.daemonz.animange.ui.dialog.PlayerMaskDialog
import com.daemonz.animange.ui.dialog.RatingDialog
import com.daemonz.animange.ui.view_helper.CustomWebClient
import com.daemonz.animange.util.AppUtils
import com.daemonz.animange.util.ITEM_STATUS_TRAILER
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.PLAYER_DEEP_LINK
import com.daemonz.animange.util.makeTextLink

import com.daemonz.animange.viewmodel.PlayerViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class PlayerFragment :
    BaseFragment<PlayerViewFragmentBinding, PlayerViewModel>(PlayerViewFragmentBinding::inflate) {
    override val viewModel: PlayerViewModel by viewModels()
    private val arg: PlayerFragmentArgs by navArgs()

    private var lastTouchWebView = 0L
    private var slug: String = ""

    private val listFrag = listOf<Fragment>(
        SuggestionFragment(),
        EpisodesFragment(),
        CommentFragment(),
        RatingsFragment()
    )

    private var pagerAdapter: PlayerPagerAdapter? = null

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val b = super.onCreateView(inflater, container, savedInstanceState)
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
        slug = arg.item
        getData()
    }

    private fun getData() {
        viewModel.loadData(slug)
        viewModel.getRatingAvg(slug)
        viewModel.getAllRating(slug)
        viewModel.loadComments(slug)
        showLoadingOverlay()
    }

    override fun setupViews() {
        binding.apply {
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
                    putExtra(Intent.EXTRA_TEXT, "$PLAYER_DEEP_LINK${slug}")
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
            btnRate.setOnClickListener {
                if (LoginData.account == null) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.user_not_logged_in),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.getRating(
                        userId = LoginData.getActiveUser()?.id.toString(),
                        slug = slug
                    )
                }
            }
            binding.btnFollow.isChecked = true
            listFrag.forEach {
                (it as? ChildPlayerFragmentActions)?.setupViewModel(viewModel)
            }
            pagerAdapter = PlayerPagerAdapter(listFrag, this@PlayerFragment)
            viewPager.adapter = pagerAdapter
            viewPager.isUserInputEnabled = false
            TabLayoutMediator(tabSuggest, viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.suggest)
                    1 -> tab.text = getString(R.string.episodes)
                    2 -> tab.text = getString(R.string.comment)
                    3 -> tab.text = getString(R.string.rating)
                }
            }.attach()
            tabSuggest.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    (activity as? MainActivity)?.showHideSystemBar(false)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

            })
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
            rateAvg.observe(viewLifecycleOwner) {
                if (it == 0.0) {
                    binding.starsLayout.isVisible = false
                } else {
                    binding.starsLayout.isVisible = true
                    binding.rateAvg.text =
                        String.format(Locale.getDefault(), "%.1f", it)
                }
            }
            comments.observe(viewLifecycleOwner) { comments ->
                ALog.d(TAG, "comments: ${comments.size}")
                binding.tabSuggest.getTabAt(2)?.apply {
                    orCreateBadge.number = comments.size
                    orCreateBadge.horizontalOffset = -2
                    if (comments.isEmpty()) {
                        orCreateBadge.isVisible = false
                    } else {
                        orCreateBadge.isVisible = true
                    }
                }
            }
            allRatings.observe(viewLifecycleOwner) { ratings ->
                ALog.d(TAG, "allRatings: ${ratings.size}")
                binding.tabSuggest.getTabAt(3)?.apply {
                    orCreateBadge.number = ratings.size
                    orCreateBadge.horizontalOffset = -2
                    if (ratings.isEmpty()) {
                        orCreateBadge.isVisible = false
                    } else {
                        orCreateBadge.isVisible = true
                    }
                }
            }
            playerData.observe(viewLifecycleOwner) {
                ALog.d(TAG, "playerData: $slug")
                if (it.data.item?.status == ITEM_STATUS_TRAILER) {
                    findNavController().popBackStack()
                    AppUtils.playYoutube(
                        requireContext(), it.data.item.trailerUrl
                    )
                } else {
                    loadPlayerData(it)
                    if (it.data.item?.slug != null && it.data.item.slug != slug) {
                        slug = it.data.item.slug
                        getData()
                        showLoadingOverlay()
                    }
                }
                hideLoadingOverlay()

            }
            currentPlaying.observe(viewLifecycleOwner) {
                ALog.d(TAG, "currentPlaying: ${it.pivot}")
                binding.videoView.loadUrl(it.getCurrentEpisodeDetail()?.url.toString())
                binding.textTitle.text = requireContext().getString(
                    R.string.player_title,
                    viewModel.playerData.value?.data?.item?.name,
                    (it.getCurrentEpisodeDetail()?.name)
                )
            }
            isFavourite.observe(viewLifecycleOwner) {
                if (it) {
                    binding.btnFollow.icon = ContextCompat.getDrawable(
                        requireContext(),
                        currentTheme.bookmarkFilledIcon()
                    )
                } else {
                    binding.btnFollow.icon =
                        ContextCompat.getDrawable(requireContext(), currentTheme.bookmarkIcon())
                }
            }
            lastRating.observe(viewLifecycleOwner) {
                ALog.d(TAG, "lastRating: $it")
                RatingDialog(
                    it,
                    onYes = { score, comment, id ->
                        viewModel.rateItem(score, comment, id)
                    }
                ).show(childFragmentManager, "RatingDialog")
            }
        }
    }

    private fun loadPlayerData(data: ListData) {
        binding.apply {
            val desc = Html.fromHtml(data.data.item?.content, Html.FROM_HTML_MODE_LEGACY)
            if (desc.length > 150) {
                val displayText = desc.substring(
                    0,
                    125
                ) + getString(R.string.three_dot) + getString(R.string.expand_text)
                textDesc.text = displayText
                textDesc.makeTextLink(
                    textLink = getString(R.string.expand_text),
                    underline = true,
                    bold = true,
                    color = ContextCompat.getColor(requireContext(), R.color.button_light),
                    onClick = {}
                )
            } else {
                textDesc.text = desc
            }
        }
    }

    fun setFilmId(slug: String) {
        ALog.d(TAG, "setFilmId: $slug -- ${viewModel.playerData.value?.data?.item?.slug}")
        if (viewModel.playerData.value?.data?.item?.slug == slug) return
        binding.root.post {
            viewModel.loadData(slug)
        }
    }

    override fun syncTheme() {
        super.syncTheme()
        binding.apply {
            tabSuggest.setBackgroundColor(currentTheme.firstActivityBackgroundColor(requireContext()))
            tabSuggest.setTabTextColors(
                currentTheme.tabTextColorDefault(requireContext()),
                currentTheme.tabTextColorSelected(requireContext())
            )
            textTitle.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            textDesc.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            rateAvg.setTextColor(currentTheme.firstActivityTextColor(requireContext()))
            btnRate.setIconResource(currentTheme.iconRate())
            btnShare.setIconResource(currentTheme.iconShare())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}