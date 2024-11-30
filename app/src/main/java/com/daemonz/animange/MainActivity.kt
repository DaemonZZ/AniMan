package com.daemonz.animange

import android.Manifest.permission.POST_NOTIFICATIONS
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.WindowInsets.Type
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.daemonz.animange.ad.GoogleMobileAdsConsentManager
import com.daemonz.animange.databinding.ActivityMainBinding
import com.daemonz.animange.entity.Activity
import com.daemonz.animange.entity.UserAction
import com.daemonz.animange.fragment.ChooseUserFragment
import com.daemonz.animange.fragment.PlayerFragment
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.CommonAction
import com.daemonz.animange.ui.NavIcon2Action
import com.daemonz.animange.ui.dialog.InternetDialog
import com.daemonz.animange.ui.dialog.LoadingOverLay
import com.daemonz.animange.ui.dialog.UpdateDialog
import com.daemonz.animange.ui.thememanager.AnimanTheme
import com.daemonz.animange.ui.thememanager.DarkTheme
import com.daemonz.animange.ui.thememanager.LightTheme
import com.daemonz.animange.util.AppThemeManager
import com.daemonz.animange.util.ConnectionLiveData
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.NIGHT_MODE_KEY
import com.daemonz.animange.util.STRING_EMPTY
import com.daemonz.animange.util.Session
import com.daemonz.animange.util.SharePreferenceManager
import com.daemonz.animange.viewmodel.LoginViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeActivity
import com.dolatkia.animatedThemeManager.ThemeAnimationListener
import com.dolatkia.animatedThemeManager.ThemeManager
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ThemeActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var sharePreferenceManager: SharePreferenceManager
    private var currentTheme: AnimanTheme = LightTheme()
    private var loadingDialog: LoadingOverLay = LoadingOverLay(LightTheme())

    private val listFragmentsWithNavbar = listOf(
        R.id.homeFragment,
        R.id.moviesFragment,
        R.id.seriesFragment,
        R.id.tvShowFragment,
        R.id.settingsFragment
    )
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private val initialLayoutComplete = AtomicBoolean(false)
    private lateinit var binding: ActivityMainBinding

    private val updateDialog: UpdateDialog by lazy { UpdateDialog() }
    private var internetDialog: InternetDialog? = null
    private var hideToolbarJob = Job()
    private var windowInsetsController: WindowInsetsControllerCompat? = null

    @Inject
    lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    @SuppressLint("SourceLockedOrientationActivity")
    private val navChangeListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            ALog.i(TAG, "onDestinationChanged: ${destination.id}")
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            if (destination.id in listFragmentsWithNavbar) {
                binding.bottomNavigation.visibility = View.VISIBLE
                toggleToolBarShowing(
                    isShow = true,
                    autoHide = destination.id != R.id.settingsFragment
                )
            } else {
                binding.bottomNavigation.visibility = View.GONE
            }
            binding.topAppBar.postDelayed({ changeToolBarAction(destination.id) }, 500)
        }
    private var lastAction: Long = 0

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
    private lateinit var connectionLiveData: ConnectionLiveData

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

    override fun getStartTheme(): AppTheme {
        val nightMode = sharePreferenceManager.getBoolean(NIGHT_MODE_KEY, false)
        val theme = if (nightMode) DarkTheme() else LightTheme()
        this@MainActivity.setTheme(theme.mainTheme())
        return theme
    }

    fun onBack() {
        val frag =
            supportFragmentManager.fragments.find { it is NavHostFragment }?.childFragmentManager?.fragments?.find { it is CommonAction }
        (frag as? CommonAction)?.onBack()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { _, insets ->
            insets
        }
        connectionLiveData = ConnectionLiveData(this)
        connectionLiveData.observe(this) { isNetworkAvailable ->
            ALog.d(TAG, "Connection status: $isNetworkAvailable")
            isNetworkAvailable?.let {
                if (!it) {
                    showInternetDialog()
                }
            }
        }
        binding.navIcon.setOnClickListener {
            onBack()
        }
        binding.navIcon2.setOnClickListener {
            val frag =
                supportFragmentManager.fragments.find { it is NavHostFragment }?.childFragmentManager?.fragments?.find { it is NavIcon2Action }
            (frag as? NavIcon2Action)?.onNavIcon2Click()
        }
        viewModel.registerSigningLauncher(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initAdmob()
        askNotificationPermission()
        loadIntent()

        viewModel.hasNewUpdate.observe(this) {
            if (it != null && !Session.isUpdateShown) {
                updateDialog.isOptional = it.isOptional
                if (!updateDialog.isAdded) {
                    updateDialog.show(supportFragmentManager, UpdateDialog.TAG)
                    updateDialog.setTheme(currentTheme)
                    Session.isUpdateShown = true
                }
            }
        }
        ALog.w(TAG, "onCreate: ${intent.data}")
        viewModel.checkForUpdate()
        setThemeAnimationListener(object : ThemeAnimationListener {
            override fun onAnimationCancel(animation: Animator) {
                //
            }

            override fun onAnimationEnd(animation: Animator) {
                val nightMode = sharePreferenceManager.getBoolean(NIGHT_MODE_KEY, false)
                val theme = if (nightMode) DarkTheme() else LightTheme()
                AppThemeManager.changeTheme(this@MainActivity, theme.mainTheme())
            }

            override fun onAnimationRepeat(animation: Animator) {
                //
            }

            override fun onAnimationStart(animation: Animator) {
                //
            }

        })
        val nightMode = sharePreferenceManager.getBoolean(NIGHT_MODE_KEY, false)
        binding.dayNightSwitch.isVisible = true
        binding.dayNightSwitch.setIsNight(nightMode, false)
        binding.dayNightSwitch.setListener { isNightMode ->
            binding.root.postDelayed({
                val theme: AnimanTheme = if (isNightMode) {
                    DarkTheme()
                } else {
                    LightTheme()
                }
                sharePreferenceManager.setBoolean(NIGHT_MODE_KEY, isNightMode)
                ThemeManager.instance.reverseChangeTheme(
                    theme,
                    binding.dayNightSwitch
                )
            }, 500L)
        }
    }

    private fun showInternetDialog() {
        internetDialog = null
        internetDialog = InternetDialog()
        internetDialog?.let {
            if (!it.isAdded) {
                it.show(supportFragmentManager, InternetDialog.TAG)
                it.setTheme(currentTheme)
            }
        }
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
    }
    fun isReadyToLoadBanner(): Boolean {
        val res =
            !initialLayoutComplete.getAndSet(true) && googleMobileAdsConsentManager.canRequestAds
        ALog.d(TAG, "isReadyToLoad: $res")
        return res
    }

    override fun onStart() {
        super.onStart()
        setupViews()
        val conMgr = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo
        if (netInfo == null) {
            showInternetDialog()
        }
        LoginData.account?.let {
            val activity = Activity(
                id = UUID.randomUUID().toString(),
                activity = UserAction.OpenApp,
                content = "${it.name} 's just open app"
            )
            viewModel.syncActivity(activity)
        }
    }

    override fun onStop() {
        super.onStop()
        LoginData.account?.let {
            val activity = Activity(
                id = UUID.randomUUID().toString(),
                activity = UserAction.CloseApp,
                content = "${it.name} 's just close app"
            )
            viewModel.syncActivity(activity)
        }
    }
    override fun syncTheme(appTheme: AppTheme) {
        currentTheme = appTheme as AnimanTheme
        ALog.d(TAG, "syncTheme: id = ${currentTheme.id()}")
        binding.apply {
            root.setBackgroundColor(currentTheme.firstActivityBackgroundColor(this@MainActivity))
            title.setTextColor(currentTheme.firstActivityTextColor(this@MainActivity))
            appLogo.setImageResource(currentTheme.appLogoLandscape())
            navIcon.setImageResource(currentTheme.iconBack())
            navIcon2.setImageResource(currentTheme.iconClose())
            bottomNavigation.setBackgroundColor(currentTheme.firstActivityBackgroundColor(this@MainActivity))
            bottomNavigation.menu.findItem(R.id.homeFragment).setIcon(
                currentTheme.homeNavIcon()
            )
            bottomNavigation.menu.findItem(R.id.moviesFragment).setIcon(
                currentTheme.cinemaNavIcon()
            )
            bottomNavigation.menu.findItem(R.id.seriesFragment).setIcon(
                currentTheme.seriesNavIcon()
            )
            bottomNavigation.menu.findItem(R.id.tvShowFragment).setIcon(
                currentTheme.tvShowNavIcon()
            )
            bottomNavigation.menu.findItem(R.id.settingsFragment).setIcon(
                currentTheme.settingNavIcon()
            )
            actionClose.setImageResource(currentTheme.iconClose())
            actionEdit.setImageResource(currentTheme.iconEdit())
            updateDialog.setTheme(currentTheme)
        }
    }

    override fun onResume() {
        super.onResume()
        findNavController(R.id.navHostFragment).addOnDestinationChangedListener(navChangeListener)
    }

    override fun onPause() {
        super.onPause()
        findNavController(R.id.navHostFragment).removeOnDestinationChangedListener(navChangeListener)
    }


    fun showLoadingOverlay(fm: FragmentManager) {
        if (loadingDialog.isAdded)
            loadingDialog.dismiss()
        val nightMode = sharePreferenceManager.getBoolean(NIGHT_MODE_KEY, false)
        val currentTheme = if (nightMode) DarkTheme() else LightTheme()
        loadingDialog = LoadingOverLay(currentTheme)
        loadingDialog.show(fm, "LoadingOverLay")
    }

    fun hideLoadingOverlay() {
        loadingDialog.hide()
    }

    private fun setupViews() {
        binding.apply {
            val navController =
                Navigation.findNavController(this@MainActivity, R.id.navHostFragment)
            bottomNavigation.setupWithNavController(navController)
            title.text = null
            actionEdit.setOnClickListener {
                val frag =
                    supportFragmentManager.fragments.find { it is NavHostFragment }?.childFragmentManager?.fragments?.find { it is ChooseUserFragment }
                (frag as? ChooseUserFragment)?.onEditEnable(true)
                actionEdit.isVisible = false
                actionClose.isVisible = true
            }
            actionClose.setOnClickListener {
                val frag =
                    supportFragmentManager.fragments.find { it is NavHostFragment }?.childFragmentManager?.fragments?.find { it is ChooseUserFragment }
                (frag as? ChooseUserFragment)?.onEditEnable(false)
                actionEdit.isVisible = true
                actionClose.isVisible = false
            }
            actionSearch.setOnClickListener {
                toggleToolBarShowing(false)
                supportFragmentManager.fragments.first().findNavController()
                    .navigate(NavGraphDirections.actionGlobalSearchFragment())
            }
            actionList.setOnClickListener {
                toggleToolBarShowing(false)
                supportFragmentManager.fragments.first().findNavController()
                    .navigate(NavGraphDirections.actionGlobalListFilterFrag())
            }
            bottomNavigation.setOnItemReselectedListener { item ->
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
    }

    @SuppressLint("WrongConstant")
    fun showHideSystemBar(isShow: Boolean) {
        if (isShow) {
            lifecycleScope.launch {
                windowInsetsController?.show(Type.systemBars())
                delay(3000)
                windowInsetsController?.hide(Type.systemBars())
            }
        } else {
            windowInsetsController?.hide(Type.systemBars())
        }

    }

    private fun showHideToolbar(isShow: Boolean) {
        binding.topAppBar.apply {
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
            showHideToolbar(!binding.topAppBar.isVisible)
        }
        if (isShow == true && autoHide) {
            lastAction = SystemClock.elapsedRealtime()
            hideToolbarJob = Job()
            CoroutineScope(Dispatchers.Main + hideToolbarJob).launch {
                delay(2000)
                if (SystemClock.elapsedRealtime() - lastAction > 2000L) {
                    showHideToolbar(false)
                }
            }
        }
        if (!autoHide) {
            hideToolbarJob.cancel()
        }
    }

    private fun changeToolBarAction(fragment: Int) {
        binding.apply {
            navIcon.isVisible = true
            navIcon2.isVisible = false
            actionList.isVisible = false
            actionSearch.isVisible = false
            when (fragment) {
                R.id.playerFragment -> {
                    topAppBar.isVisible = true
                    topAppBar.fitsSystemWindows = false
                    title.text = STRING_EMPTY
                    actionEdit.isVisible = false
                    appLogo.isVisible = false
                    dayNightSwitch.isInvisible = true
                }

                R.id.favouritesFragment -> {
                    topAppBar.isVisible = true
                    topAppBar.fitsSystemWindows = false
                    title.text = getString(R.string.favourites_filmes)
                    title.isVisible = true
                    toggleToolBarShowing(isShow = true, autoHide = false)
                    actionEdit.isVisible = false
                    appLogo.isVisible = false
                    dayNightSwitch.isInvisible = true
                }

                R.id.profileFragment -> {
                    topAppBar.isVisible = true
                    appLogo.isVisible = false
                    title.isVisible = true
                    topAppBar.fitsSystemWindows = false
                    title.text = getString(R.string.user_profile)
                    toggleToolBarShowing(isShow = true, autoHide = false)
                    actionEdit.isVisible = false
                    dayNightSwitch.isInvisible = true
                }

                R.id.chooseUserFragment -> {
                    topAppBar.isVisible = true
                    appLogo.isVisible = false
                    title.isVisible = true
                    topAppBar.fitsSystemWindows = false
                    title.text = getString(R.string.who_watching)
                    toggleToolBarShowing(isShow = true, autoHide = false)
                    actionEdit.isVisible = true
                    dayNightSwitch.isInvisible = true
                }

                R.id.userInfoFragment, R.id.chooseAvatarFragment, R.id.supportFragment, R.id.searchFilterFragment,
                R.id.accountListFragment, R.id.adminFragment, R.id.activitiesFragment,
                R.id.languageFragment, R.id.notiFragment -> {
                    topAppBar.isVisible = true
                    appLogo.isVisible = false
                    title.isVisible = true
                    topAppBar.fitsSystemWindows = false
                    toggleToolBarShowing(isShow = true, autoHide = false)
                    actionEdit.isVisible = false
                    actionClose.isVisible = false
                    dayNightSwitch.isInvisible = true
                }
                R.id.pinInputFragment -> {
                    topAppBar.isVisible = true
                    navIcon.visibility = View.INVISIBLE
                    navIcon2.isVisible = true
                    appLogo.isVisible = false
                    title.isVisible = true
                    topAppBar.fitsSystemWindows = false
                    toggleToolBarShowing(isShow = true, autoHide = false)
                    actionEdit.isVisible = false
                    actionClose.isVisible = false
                    dayNightSwitch.isInvisible = true
                }

                R.id.themeFragment -> {
                    toggleToolBarShowing(false)
                    dayNightSwitch.isInvisible = true
                }

                R.id.settingsFragment -> {
                    appLogo.isVisible = true
                    navIcon.isVisible = false
                    title.isVisible = false
                    dayNightSwitch.isVisible = true
                    toggleToolBarShowing(isShow = true, autoHide = false)
                }
                R.id.searchFragment -> {
                    toggleToolBarShowing(isShow = false)
                }

                else -> {
                    topAppBar.isVisible = true
                    navIcon.isVisible = false
                    appLogo.isVisible = true
                    topAppBar.fitsSystemWindows = true
                    actionSearch.isVisible = true
                    actionList.isVisible = false
                    title.text = STRING_EMPTY
                    actionEdit.isVisible = false
                    dayNightSwitch.isInvisible = true
                }
            }

        }
    }


    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}

        // Load an ad.
        if (initialLayoutComplete.get()) {
//            val frag =
//                supportFragmentManager.fragments.find { it is NavHostFragment }?.childFragmentManager?.fragments?.find { it is AdmobHandler }
//            (frag as? AdmobHandler)?.loadBanner()
            (application as? AnimanApp)?.loadAd(this)
        }
    }

    fun setTitle(title: String) {
        binding.title.text = title
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        ALog.d(TAG, "onNewIntent ${intent.data}")
        if (intent.action == Intent.ACTION_VIEW) {
            loadIntent()
        }
    }

    override fun onDestroy() {
        ALog.d(TAG, "onDestroy")
        super.onDestroy()
    }
}