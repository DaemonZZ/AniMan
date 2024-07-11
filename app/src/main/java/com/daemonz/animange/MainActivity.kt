package com.daemonz.animange

import android.Manifest.permission.POST_NOTIFICATIONS
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.WindowMetrics
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.daemonz.animange.ad.GoogleMobileAdsConsentManager
import com.daemonz.animange.databinding.ActivityMainBinding
import com.daemonz.animange.fragment.ChooseUserFragment
import com.daemonz.animange.fragment.PlayerFragment
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.BottomNavigationAction
import com.daemonz.animange.ui.dialog.LoadingOverLay
import com.daemonz.animange.ui.dialog.UpdateDialog
import com.daemonz.animange.util.AdmobConst
import com.daemonz.animange.util.AdmobConstTest
import com.daemonz.animange.util.NIGHT_MODE_KEY
import com.daemonz.animange.util.SharePreferenceManager
import com.daemonz.animange.util.ThemeManager
import com.daemonz.animange.viewmodel.LoginViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var sharePreferenceManager: SharePreferenceManager

    private val loadingRequest = mutableSetOf<String>()
    private val loadingDialog: LoadingOverLay by lazy {
        LoadingOverLay()
    }
    private var topAppBar: MaterialToolbar? = null
    private var appBarLayout: AppBarLayout? = null
    private var bottomNavigation: BottomNavigationView? = null
    private val listFragmentsWithNavbar = listOf(
        R.id.homeFragment,
        R.id.moviesFragment,
        R.id.seriesFragment,
        R.id.tvShowFragment,
        R.id.settingsFragment
    )
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private val initialLayoutComplete = AtomicBoolean(false)
    private var adView: AdView? = null
    private lateinit var binding: ActivityMainBinding

    private val updateDialog: UpdateDialog by lazy { UpdateDialog() }

    @Inject
    lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
    private val navChangeListener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            ALog.i(TAG, "onDestinationChanged: ${destination.id}")
            if (destination.id in listFragmentsWithNavbar) {
                bottomNavigation?.visibility = View.VISIBLE
                binding.adViewContainer.isVisible = true
                toggleToolBarShowing(
                    isShow = destination.id != R.id.settingsFragment,
                    autoHide = true
                )
            } else {
                bottomNavigation?.visibility = View.GONE
                binding.adViewContainer.isVisible = false
            }
            appBarLayout?.postDelayed({ changeToolBarAction(destination.id) }, 500)
        }
    private var lastAction: Long = 0
    private var lastLoadingAction: Long = 0

    private val adSize: AdSize
        get() {
            val displayMetrics = resources.displayMetrics
            val windowMetrics: WindowMetrics = this.windowManager.currentWindowMetrics
            val adWidthPixels = windowMetrics.bounds.width()
            val density = displayMetrics.density
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new FCM registration token
                    val token = task.result

                    // Log and toast
                    val msg = getString(R.string.msg_token_fmt, token)
                    ALog.d(TAG, msg)
                })
                Firebase.messaging.subscribeToTopic("news")
                    .addOnCompleteListener { task ->
                        var msg = "Subscribed"
                        if (!task.isSuccessful) {
                            msg = "Subscribe failed"
                        }
                        Log.d(TAG, msg)
                    }
            } else if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val nightMode = sharePreferenceManager.getBoolean(NIGHT_MODE_KEY, false)
        ThemeManager.setNightMode(nightMode)
        val currentTheme = ThemeManager.getTheme(sharePreferenceManager)
        theme.applyStyle(currentTheme, true)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { _, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }
        viewModel.registerSigningLauncher(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        topAppBar = findViewById(R.id.topAppBar)
        appBarLayout = findViewById(R.id.app_bar_layout)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        initAdmob()
        askNotificationPermission()
        loadIntent()
    }

    private fun loadIntent() {
        ALog.i(TAG, "loadIntent: ${intent.action} - ${intent.data?.schemeSpecificPart}")
        val slug = intent.data?.getQueryParameter("slug")
        if (!slug.isNullOrEmpty()) {
            val frag =
                supportFragmentManager.fragments.first().childFragmentManager.fragments.firstOrNull { it is PlayerFragment }
            if (frag != null) {
                (frag as PlayerFragment).setFilmId(slug)
            } else {
                binding.root.post {
                    supportFragmentManager.fragments.first().findNavController()
                        .navigate(NavGraphDirections.actionGlobalPlayerFragment(slug))
                }
            }
        }
    }

    private fun initAdmob() {
        adView = AdView(this)
        binding.adViewContainer.addView(adView)

        // Log the Mobile Ads SDK version.
        Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion())

        googleMobileAdsConsentManager.gatherConsent(this) { error ->
            if (error != null) {
                // Consent not obtained in current session.
                Log.d(TAG, "${error.errorCode}: ${error.message}")
            }

            if (googleMobileAdsConsentManager.canRequestAds) {
                initializeMobileAdsSdk()
            }

            if (googleMobileAdsConsentManager.isPrivacyOptionsRequired) {
                // Regenerate the options menu to include a privacy setting.
                invalidateOptionsMenu()
            }
        }

        // This sample attempts to load ads using consent obtained in the previous session.
        if (googleMobileAdsConsentManager.canRequestAds) {
            initializeMobileAdsSdk()
        }

        // Since we're loading the banner based on the adContainerView size, we need to wait until this
        // view is laid out before we can get the width.
        binding.adViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete.getAndSet(true) && googleMobileAdsConsentManager.canRequestAds) {
                loadBanner()
            }
        }

        // Set your test devices. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device."
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(
                listOf(
                    "5A7A56B8A70FADAD6FF94C6617C41DA3",
                    "FEECD2427985C5CD053E9C99665EAE27",
                    "F12EEE26A51EF399CABF10ABE9B8EDE0",
                    "ED514B0081C81DE13EADBC68CDAFD2FF"
                )
            ).build()
        )
        viewModel.hasNewUpdate.observe(this) {
            if (it != null) {
                updateDialog.isOptional = it.isOptional
                if (!updateDialog.isAdded) {
                    updateDialog.show(supportFragmentManager, UpdateDialog.TAG)
                }
            }
        }
        ALog.w(TAG, "onCreate: ${intent.data}")
    }

    override fun onStart() {
        super.onStart()
        setupViews()
        viewModel.checkForUpdate()
    }

    override fun onResume() {
        super.onResume()
        findNavController(R.id.navHostFragment).addOnDestinationChangedListener(navChangeListener)
        adView?.resume()
    }

    override fun onPause() {
        super.onPause()
        findNavController(R.id.navHostFragment).removeOnDestinationChangedListener(navChangeListener)
        adView?.pause()
    }


    fun showLoadingOverlay(fm: FragmentManager, id: String) {
        if (SystemClock.elapsedRealtime() - lastLoadingAction > 1000) {
            ALog.d(TAG, "showLoadingOverlay $id dd: ${loadingRequest.size}")
            loadingRequest.add(id)
            if (!loadingDialog.isAdded && loadingRequest.size == 1) {
                ALog.d(TAG, "showLoadingOverlay showed")
                loadingDialog.show(fm, "LoadingOverLay")
            }
        }
        lastLoadingAction = SystemClock.elapsedRealtime()
    }

    fun hideLoadingOverlay(id: String) {
        ALog.d(TAG, "hideLoadingOverlay $id dd: ${loadingRequest.size}")
        loadingRequest.remove(id)
        if ((loadingRequest.isEmpty() || id.isEmpty()) && loadingDialog.isAdded) {
            loadingDialog.dismiss()
        }
    }

    private fun setupViews() {
        val navController = Navigation.findNavController(this@MainActivity, R.id.navHostFragment)
        bottomNavigation?.setupWithNavController(navController)
        setSupportActionBar(topAppBar)
        supportActionBar?.title = null
        topAppBar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    val navFrag = supportFragmentManager.fragments.find { it is NavHostFragment }
                    navFrag?.childFragmentManager?.fragments?.forEach {
                        (it as? BottomNavigationAction)?.onSearch()
                    }
                    true
                }

                R.id.edit -> {
                    val frag =
                        supportFragmentManager.fragments.find { it is NavHostFragment }?.childFragmentManager?.fragments?.find { it is ChooseUserFragment }
                    (frag as? ChooseUserFragment)?.onEditEnable(true)
                    topAppBar?.menu?.findItem(R.id.edit)?.isVisible = false
                    topAppBar?.menu?.findItem(R.id.close)?.isVisible = true
                    true
                }

                R.id.close -> {
                    val frag =
                        supportFragmentManager.fragments.find { it is NavHostFragment }?.childFragmentManager?.fragments?.find { it is ChooseUserFragment }
                    (frag as? ChooseUserFragment)?.onEditEnable(false)
                    topAppBar?.menu?.findItem(R.id.edit)?.isVisible = true
                    topAppBar?.menu?.findItem(R.id.close)?.isVisible = false
                    true
                }

                else -> false
            }
        }
//            NavigationBarView.OnItemSelectedListener { item ->
//
//                true
//            }
        bottomNavigation?.setOnItemReselectedListener { item ->
            when (item.itemId) {
//                    R.id.item_1 -> {
//                        // Respond to navigation item 1 reselection
//                    }
//                    R.id.item_2 -> {
//                        // Respond to navigation item 2 reselection
//                    }
            }
        }
    }

    private fun showHideToolbar(isShow: Boolean) {
        appBarLayout?.apply {
            if (isShow) {
                animate().translationY(0f).alpha(1f).setDuration(200)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            super.onAnimationStart(animation)
                            isVisible = true
                        }
                    })
            } else {
                animate().translationY(-this.height.toFloat()).alpha(0f).setDuration(200)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            isVisible = false
                        }
                    })
            }
        }
    }

    fun toggleToolBarShowing(isShow: Boolean? = null, autoHide: Boolean = false) {
        isShow?.let {
            showHideToolbar(it)
        } ?: kotlin.run {
            showHideToolbar(appBarLayout?.isVisible != true)
        }
        if (isShow == true && autoHide) {
            lastAction = SystemClock.elapsedRealtime()
            appBarLayout?.postDelayed({
                if (SystemClock.elapsedRealtime() - lastAction > 2000L) {
                    showHideToolbar(false)
                }
            }, 2000)
        }
    }

    private fun changeToolBarAction(fragment: Int) {
        when (fragment) {
            R.id.playerFragment -> {
                topAppBar?.isVisible = true
                topAppBar?.navigationIcon =
                    ResourcesCompat.getDrawable(resources, R.drawable.arrow_back, null)
                topAppBar?.setNavigationOnClickListener {
                    val navController = findNavController(R.id.navHostFragment)
                    navController.popBackStack()
                }
                topAppBar?.fitsSystemWindows = false
                topAppBar?.menu?.findItem(R.id.search)?.isVisible = false
                topAppBar?.title = ""
                topAppBar?.menu?.findItem(R.id.edit)?.isVisible = false
            }

            R.id.favouritesFragment -> {
                topAppBar?.isVisible = true
                topAppBar?.navigationIcon =
                    ResourcesCompat.getDrawable(resources, R.drawable.arrow_back, null)
                topAppBar?.setNavigationOnClickListener {
                    val navController = findNavController(R.id.navHostFragment)
                    navController.popBackStack()
                }
                topAppBar?.fitsSystemWindows = false
                topAppBar?.menu?.findItem(R.id.search)?.isVisible = false
                topAppBar?.title = getString(R.string.favourites_filmes)
                toggleToolBarShowing(isShow = true, autoHide = false)
                topAppBar?.menu?.findItem(R.id.edit)?.isVisible = false
            }

            R.id.profileFragment -> {
                topAppBar?.isVisible = true
                topAppBar?.navigationIcon =
                    ResourcesCompat.getDrawable(resources, R.drawable.arrow_back, null)
                topAppBar?.setNavigationOnClickListener {
                    val navController = findNavController(R.id.navHostFragment)
                    navController.popBackStack()
                }
                topAppBar?.fitsSystemWindows = false
                topAppBar?.menu?.findItem(R.id.search)?.isVisible = false
                topAppBar?.title = getString(R.string.user_profile)
                toggleToolBarShowing(isShow = true, autoHide = false)
                topAppBar?.menu?.findItem(R.id.edit)?.isVisible = false
            }

            R.id.chooseUserFragment -> {
                topAppBar?.isVisible = true
                topAppBar?.navigationIcon =
                    ResourcesCompat.getDrawable(resources, R.drawable.arrow_back, null)
                topAppBar?.setNavigationOnClickListener {
                    val navController = findNavController(R.id.navHostFragment)
                    navController.popBackStack()
                }
                topAppBar?.fitsSystemWindows = false
                topAppBar?.menu?.findItem(R.id.search)?.isVisible = false
                topAppBar?.title = getString(R.string.who_watching)
                toggleToolBarShowing(isShow = true, autoHide = false)
                topAppBar?.menu?.findItem(R.id.edit)?.isVisible = true
            }

            R.id.userInfoFragment, R.id.chooseAvatarFragment -> {
                topAppBar?.isVisible = true
                topAppBar?.navigationIcon =
                    ResourcesCompat.getDrawable(resources, R.drawable.arrow_back, null)
                topAppBar?.setNavigationOnClickListener {
                    val navController = findNavController(R.id.navHostFragment)
                    navController.popBackStack()
                }
                topAppBar?.fitsSystemWindows = false
                topAppBar?.menu?.findItem(R.id.search)?.isVisible = false
                toggleToolBarShowing(isShow = true, autoHide = false)
                topAppBar?.menu?.findItem(R.id.edit)?.isVisible = false
                topAppBar?.menu?.findItem(R.id.close)?.isVisible = false
            }

            R.id.themeFragment -> {
                toggleToolBarShowing(false)
            }
            else -> {
                topAppBar?.isVisible = true
                topAppBar?.navigationIcon =
                    ResourcesCompat.getDrawable(resources, R.drawable.app_logo, null)
                topAppBar?.setNavigationOnClickListener {
                    //
                }
                topAppBar?.fitsSystemWindows = true
                topAppBar?.menu?.findItem(R.id.search)?.isVisible = true
                topAppBar?.title = ""
                topAppBar?.menu?.findItem(R.id.edit)?.isVisible = false
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)

    }

    private fun loadBanner() {
        ALog.v(TAG, "loadBanner")
        // This is an ad unit ID for a test ad. Replace with your own banner ad unit ID.
        adView?.adUnitId =
            if (BuildConfig.BUILD_TYPE == "release") AdmobConst.BANNER_AD_ADAPTIVE else AdmobConstTest.BANNER_AD_ADAPTIVE
        adView?.setAdSize(adSize)

        // Create an ad request.
        val adRequest = AdRequest.Builder().build()

        ALog.v(TAG, "adRequest:isTestDevice: ${adRequest.isTestDevice(this)}")

        // Start loading the ad in the background.
        adView?.loadAd(adRequest)
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}

        // Load an ad.
        if (initialLayoutComplete.get()) {
            loadBanner()
        }
    }

    fun setTitle(title: String) {
        topAppBar?.title = title
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        ALog.d(TAG, "onNewIntent ${intent.data}")
        if (intent.action == Intent.ACTION_VIEW) {
            loadIntent()
        }
    }

    override fun onDestroy() {
        adView?.destroy()
        super.onDestroy()
    }
}