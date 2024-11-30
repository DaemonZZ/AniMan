package com.daemonz.animange.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.MainActivity
import com.daemonz.animange.R
import com.daemonz.animange.base.BaseFragment
import com.daemonz.animange.databinding.FragmentPlayerBinding
import com.daemonz.animange.entity.Comment
import com.daemonz.animange.entity.Episode
import com.daemonz.animange.entity.FilmRating
import com.daemonz.animange.entity.ListData
import com.daemonz.animange.fragment.player.ChildPlayerFragmentActions
import com.daemonz.animange.fragment.player.CommentFragment
import com.daemonz.animange.fragment.player.EpisodesFragment
import com.daemonz.animange.fragment.player.RatingsFragment
import com.daemonz.animange.fragment.player.SuggestionFragment
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.InterstitialAdHandler
import com.daemonz.animange.ui.adapter.PlayerPagerAdapter
import com.daemonz.animange.ui.custom.DzWebView
import com.daemonz.animange.ui.dialog.FilmInfoDialog
import com.daemonz.animange.ui.dialog.PlayerMaskDialog
import com.daemonz.animange.ui.dialog.RatingDialog
import com.daemonz.animange.ui.view_helper.CustomWebClient
import com.daemonz.animange.util.AdmobConst
import com.daemonz.animange.util.AdmobConstTest
import com.daemonz.animange.util.AppUtils
import com.daemonz.animange.util.ITEM_STATUS_TRAILER
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.PLAYER_DEEP_LINK
import com.daemonz.animange.util.makeTextLink
import com.daemonz.animange.viewmodel.PlayerViewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class PlayerFragment :
    BaseFragment<FragmentPlayerBinding, PlayerViewModel>(FragmentPlayerBinding::inflate),
    InterstitialAdHandler {
    override val viewModel: PlayerViewModel by viewModels()
    private val arg: PlayerFragmentArgs by navArgs()

    private var lastTouchWebView = 0L
    private var slug: String = ""
    private var interstitialAd: InterstitialAd? = null
    private var videoView: DzWebView? = null

    private val listFragLandscape = listOf<Fragment>(
        SuggestionFragment(),
        EpisodesFragment(),
        CommentFragment(),
        RatingsFragment()
    )
    private val listFragPortrait = listOf<Fragment>(
        SuggestionFragment(),
        EpisodesFragment(),
        CommentFragment(),
        RatingsFragment()
    )

    private var pagerAdapter: PlayerPagerAdapter? = null

    private var hideToolbarJob = Job()
    private var lastAction: Long = 0

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val b = super.onCreateView(inflater, container, savedInstanceState)
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        videoView = DzWebView(requireContext())
        requireActivity().resources.configuration.orientation.let {
            binding.viewLandscape.root.isVisible = it == Configuration.ORIENTATION_LANDSCAPE
            binding.viewPortrait.root.isVisible = it == Configuration.ORIENTATION_PORTRAIT
            if (it == Configuration.ORIENTATION_LANDSCAPE) {
                binding.viewLandscape.videoViewContainer.addView(videoView)
                toggleToolBarShowing(false)
                showActionBarLandScape()
            } else {
                binding.viewPortrait.videoViewContainer.addView(videoView)
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
        videoView?.webViewClient = object : WebViewClient() {
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
        videoView?.setOnTouchListener { v, event ->
            if (SystemClock.elapsedRealtime() - lastTouchWebView > 1000) {
                if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    ALog.d(TAG, "click on webview + ${event.action}")
                    toggleToolBarShowing(
                        isShow = true, autoHide = true
                    )
                } else {
                    showActionBarLandScape()
                }
            }
            lastTouchWebView = SystemClock.elapsedRealtime()
            v.onTouchEvent(event)
            true
        }
        videoView?.settings?.apply {
            javaScriptEnabled = true
            useWideViewPort = false
        }
        videoView?.webChromeClient = CustomWebClient(showWebView = {
            videoView?.visibility = View.VISIBLE
        }, hideWebView = { fullscreen ->
            videoView?.visibility = View.GONE
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
        setupViewsLandscape()
        setupViewsPortrait()
        showInterstitial()
    }

    private fun setupViewsLandscape() {
        binding.viewLandscape.apply {
            navIcon.setOnClickListener {
                onBack()
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
            btnFollow.isChecked = true
            listFragLandscape.forEach {
                (it as? ChildPlayerFragmentActions)?.setupViewModel(viewModel)
            }
            pagerAdapter = PlayerPagerAdapter(listFragLandscape, this@PlayerFragment)
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

    private fun setupViewsPortrait() {
        binding.viewPortrait.apply {
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
            btnFollow.isChecked = true
            listFragPortrait.forEach {
                (it as? ChildPlayerFragmentActions)?.setupViewModel(viewModel)
            }
            pagerAdapter = PlayerPagerAdapter(listFragPortrait, this@PlayerFragment)
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

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        binding.videoView.saveState(outState)
//    }
//
//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        if (savedInstanceState != null) {
//            binding.videoView.restoreState(savedInstanceState)
//        }
//    }

    override fun setupObservers() {
        viewModel.apply {
            rateAvg.observe(viewLifecycleOwner) {
                updateRatePortrait(it)
                updateRateLandscape(it)
            }
            comments.observe(viewLifecycleOwner) { comments ->
                updateCommentPortrait(comments)
                updateCommentLandscape(comments)
            }
            allRatings.observe(viewLifecycleOwner) { ratings ->
                updateAllRatingPortrait(ratings)
                updateAllRatingLandscape(ratings)
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
                videoView?.loadUrl(it.getCurrentEpisodeDetail()?.url.toString())
                updateCurrentPlayingPortrait(it)
                updateCurrentPlayingLandscape(it)
            }
            isFavourite.observe(viewLifecycleOwner) {
                updateIsFavouritePortrait(it)
                updateIsFavouriteLandscape(it)
            }
            lastRating.observe(viewLifecycleOwner) {
                ALog.d(TAG, "lastRating: $it")
                RatingDialog(
                    it,
                    onYes = { score, comment, id ->
                        viewModel.rateItem(score, comment, id)
                        binding.viewLandscape.start1.setImageResource(R.drawable.star_filled)
                        binding.viewPortrait.start1.setImageResource(R.drawable.star_filled)
                    },
                    currentTheme
                ).show(childFragmentManager, "RatingDialog")
            }
        }
    }

    private fun updateIsFavouritePortrait(isFavourite: Boolean) {
        if (isFavourite) {
            binding.viewPortrait.btnFollow.icon = ContextCompat.getDrawable(
                requireContext(),
                currentTheme.bookmarkFilledIcon()
            )
        } else {
            binding.viewPortrait.btnFollow.icon =
                ContextCompat.getDrawable(requireContext(), currentTheme.bookmarkIcon())
        }
    }

    private fun updateIsFavouriteLandscape(isFavourite: Boolean) {
        if (isFavourite) {
            binding.viewLandscape.btnFollow.icon = ContextCompat.getDrawable(
                requireContext(),
                currentTheme.bookmarkFilledIcon()
            )
        } else {
            binding.viewLandscape.btnFollow.icon =
                ContextCompat.getDrawable(requireContext(), currentTheme.bookmarkIcon())
        }
    }

    private fun updateCurrentPlayingPortrait(currentPlaying: Episode) {
        ALog.d(TAG, "currentPlaying: ${currentPlaying.pivot}")
        binding.viewPortrait.textTitle.text = requireContext().getString(
            R.string.player_title,
            viewModel.playerData.value?.data?.item?.name,
            (currentPlaying.getCurrentEpisodeDetail()?.name)
        )
    }

    private fun updateCurrentPlayingLandscape(currentPlaying: Episode) {
        ALog.d(TAG, "currentPlaying: ${currentPlaying.pivot}")
        binding.viewLandscape.textTitle.text = requireContext().getString(
            R.string.player_title,
            viewModel.playerData.value?.data?.item?.name,
            (currentPlaying.getCurrentEpisodeDetail()?.name)
        )
    }

    private fun updateAllRatingPortrait(ratings: List<FilmRating>) {
        ALog.d(TAG, "allRatings: ${ratings.size}")
        binding.viewPortrait.tabSuggest.getTabAt(3)?.apply {
            orCreateBadge.number = ratings.size
            orCreateBadge.horizontalOffset = -2
            if (ratings.isEmpty()) {
                orCreateBadge.isVisible = false
            } else {
                orCreateBadge.isVisible = true
            }
        }
    }

    private fun updateAllRatingLandscape(ratings: List<FilmRating>) {
        ALog.d(TAG, "allRatings: ${ratings.size}")
        binding.viewLandscape.tabSuggest.getTabAt(3)?.apply {
            orCreateBadge.number = ratings.size
            orCreateBadge.horizontalOffset = -2
            if (ratings.isEmpty()) {
                orCreateBadge.isVisible = false
            } else {
                orCreateBadge.isVisible = true
            }
        }
    }

    private fun updateCommentPortrait(comments: List<Comment>) {
        ALog.d(TAG, "comments: ${comments.size}")
        binding.viewPortrait.tabSuggest.getTabAt(2)?.apply {
            orCreateBadge.number = comments.size
            orCreateBadge.horizontalOffset = -2
            if (comments.isEmpty()) {
                orCreateBadge.isVisible = false
            } else {
                orCreateBadge.isVisible = true
            }
        }
    }

    private fun updateCommentLandscape(comments: List<Comment>) {
        ALog.d(TAG, "comments: ${comments.size}")
        binding.viewLandscape.tabSuggest.getTabAt(2)?.apply {
            orCreateBadge.number = comments.size
            orCreateBadge.horizontalOffset = -2
            if (comments.isEmpty()) {
                orCreateBadge.isVisible = false
            } else {
                orCreateBadge.isVisible = true
            }
        }
    }

    private fun updateRateLandscape(rate: Double) {
        if (rate == 0.0)
            binding.viewLandscape.start1.setImageResource(R.drawable.star_outline_36)
        binding.viewLandscape.rateAvg.text =
            String.format(Locale.getDefault(), "%.1f", rate)
    }

    private fun updateRatePortrait(rate: Double) {
        if (rate == 0.0)
            binding.viewPortrait.start1.setImageResource(R.drawable.star_outline_36)
        binding.viewPortrait.rateAvg.text =
            String.format(Locale.getDefault(), "%.1f", rate)
    }

    @SuppressLint("SetTextI18n")
    private fun loadPlayerData(data: ListData) {
        loadPlayerDataPortrait(data)
        loadPlayerDataLandscape(data)
    }

    @SuppressLint("SetTextI18n")
    private fun loadPlayerDataLandscape(data: ListData) {
        binding.viewLandscape.apply {
            val desc = Html.fromHtml(data.data.item?.content, Html.FROM_HTML_MODE_LEGACY)
            if (desc.length > 150) {
                textDesc.text = desc.substring(
                    0,
                    125
                ) + getString(R.string.three_dot) + getString(R.string.expand_text)
            } else {
                textDesc.text = desc.toString()
                    .trim() + getString(R.string.three_dot) + getString(R.string.expand_text)
            }
            textDesc.makeTextLink(
                textLink = getString(R.string.expand_text),
                underline = true,
                bold = true,
                color = ContextCompat.getColor(requireContext(), R.color.button_light),
                onClick = {
                    viewModel.playerData.value?.let {
                        FilmInfoDialog(currentTheme, it).show(
                            childFragmentManager,
                            TAG
                        )
                    }
                }
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadPlayerDataPortrait(data: ListData) {
        binding.viewPortrait.apply {
            val desc = Html.fromHtml(data.data.item?.content, Html.FROM_HTML_MODE_LEGACY)
            if (desc.length > 150) {
                textDesc.text = desc.substring(
                    0,
                    125
                ) + getString(R.string.three_dot) + getString(R.string.expand_text)
            } else {
                textDesc.text = desc.toString()
                    .trim() + getString(R.string.three_dot) + getString(R.string.expand_text)
            }
            textDesc.makeTextLink(
                textLink = getString(R.string.expand_text),
                underline = true,
                bold = true,
                color = ContextCompat.getColor(requireContext(), R.color.button_light),
                onClick = {
                    viewModel.playerData.value?.let {
                        FilmInfoDialog(currentTheme, it).show(
                            childFragmentManager,
                            TAG
                        )
                    }
                }
            )
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
        syncThemeLandscape()
        syncThemePortrait()
    }

    private fun syncThemeLandscape() {
        binding.viewLandscape.apply {
            root.setBackgroundColor(currentTheme.firstActivityBackgroundColor(requireContext()))
            tabSuggest.setBackgroundColor(
                currentTheme.firstActivityBackgroundColor(
                    requireContext()
                )
            )
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

    private fun syncThemePortrait() {
        binding.viewPortrait.apply {
            root.setBackgroundColor(currentTheme.firstActivityBackgroundColor(requireContext()))
            tabSuggest.setBackgroundColor(
                currentTheme.firstActivityBackgroundColor(
                    requireContext()
                )
            )
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

    private var adIsLoading: Boolean = false
    override fun loadAd() {
        // Request a new ad if one isn't already loaded.
        if (adIsLoading || interstitialAd != null) {
            return
        }
        adIsLoading = true

        val adRequest = AdRequest.Builder().build()
        val adId =
            if (BuildConfig.BUILD_TYPE == "release") AdmobConst.INTERSTITIAL_AD_ID else AdmobConstTest.INTERSTITIAL_AD_ID
        InterstitialAd.load(
            requireContext(),
            adId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    ALog.d(TAG, adError.message)
                    interstitialAd = null
                    adIsLoading = false
                    val error =
                        "domain: ${adError.domain}, code: ${adError.code}, " + "message: ${adError.message}"
                    ALog.e(TAG, error)
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    ALog.d(TAG, "Ad was loaded.")
                    interstitialAd = ad
                    adIsLoading = false
                    showInterstitial()
                }
            },
        )
    }

    override fun showInterstitial() {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        ALog.d(TAG, "Ad was dismissed.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        interstitialAd = null
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        ALog.d(TAG, "Ad failed to show.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        interstitialAd = null
                    }

                    override fun onAdShowedFullScreenContent() {
                        ALog.d(TAG, "Ad showed fullscreen content.")
                        // Called when ad is dismissed.
                    }
                }
            interstitialAd?.show(requireActivity())
        } else {
            if (googleMobileAdsConsentManager.canRequestAds) {
                loadAd()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        ALog.d(TAG, "onConfigurationChanged: ${newConfig.orientation}")
        newConfig.orientation.let {
            binding.viewLandscape.root.isVisible = it == Configuration.ORIENTATION_LANDSCAPE
            binding.viewPortrait.root.isVisible = it == Configuration.ORIENTATION_PORTRAIT
            if (it == Configuration.ORIENTATION_LANDSCAPE) {
                binding.viewPortrait.videoViewContainer.removeAllViews()
                try {
                    if (videoView?.isAttached == false) {
                        binding.viewLandscape.videoViewContainer.addView(videoView)
                    } else {
                        lifecycleScope.launch {
                            delay(500)
                            binding.viewLandscape.videoViewContainer.addView(videoView)
                        }
                    }
                } catch (e: Exception) {
                    ALog.e(TAG, "Error rotated view")
                    e.printStackTrace()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.rotate_error),
                        Toast.LENGTH_LONG
                    ).show()
                }

                toggleToolBarShowing(false)
                showActionBarLandScape()
            } else {
                binding.viewLandscape.videoViewContainer.removeAllViews()
                try {
                    if (videoView?.isAttached == false) {
                        binding.viewPortrait.videoViewContainer.addView(videoView)
                    } else {
                        lifecycleScope.launch {
                            delay(500)
                            binding.viewPortrait.videoViewContainer.addView(videoView)
                        }
                    }
                } catch (e: Exception) {
                    ALog.e(TAG, "Error rotated view")
                    e.printStackTrace()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.rotate_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
                toggleToolBarShowing(true, autoHide = true)
            }
        }
    }

    private fun showActionBarLandScape() {
        ALog.d(TAG, "showActionBarLandScape")
        binding.viewLandscape.topAppBar.animate().translationY(0f).alpha(1f).setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    binding.viewLandscape.topAppBar.isVisible = true
                }
            })
        lastAction = SystemClock.elapsedRealtime()
        hideToolbarJob = Job()
        CoroutineScope(Dispatchers.Main + hideToolbarJob).launch {
            delay(2000)
            if (SystemClock.elapsedRealtime() - lastAction > 2000L) {
                binding.viewLandscape.topAppBar.animate()
                    .translationY(-binding.viewLandscape.topAppBar.height.toFloat()).alpha(0f)
                    .setDuration(200)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            binding.viewLandscape.topAppBar.isVisible = false
                        }
                    })
            }
        }
    }
}