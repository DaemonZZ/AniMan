package com.daemonz.animange

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.os.SystemClock
import android.view.Menu
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.daemonz.animange.fragment.PlayerFragment
import com.daemonz.animange.log.ALog
import com.daemonz.animange.ui.BottomNavigationAction
import com.daemonz.animange.ui.dialog.LoadingOverLay
import com.daemonz.animange.viewmodel.HomeViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    val viewModel: HomeViewModel by viewModels()
    private val loadingRequest = mutableSetOf<String>()
    private val loadingDialog: LoadingOverLay by lazy {
        LoadingOverLay()
    }
    private var topAppBar: MaterialToolbar? = null
    private var appBarLayout: AppBarLayout? = null
    private var bottomNavigation: BottomNavigationView? = null
    private val listFragmentsWithNavbar = listOf(
        R.id.tab1Fragment,
        R.id.tab2Fragment,
        R.id.tab3Fragment,
        R.id.tab4Fragment,
        R.id.tab5Fragment,
    )
    private val navChangeListener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            ALog.i(TAG, "onDestinationChanged: ${destination.id}")
            if (destination.id in listFragmentsWithNavbar) {
                bottomNavigation?.visibility = View.VISIBLE
            } else {
                bottomNavigation?.visibility = View.GONE
            }
        }
    private var lastAction: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        topAppBar = findViewById(R.id.topAppBar)
        appBarLayout = findViewById(R.id.app_bar_layout)
        bottomNavigation = findViewById(R.id.bottom_navigation)
    }

    override fun onStart() {
        super.onStart()
        setupViews()
    }

    override fun onResume() {
        super.onResume()
        findNavController(R.id.navHostFragment).addOnDestinationChangedListener(navChangeListener)
    }

    override fun onPause() {
        super.onPause()
        findNavController(R.id.navHostFragment).removeOnDestinationChangedListener(navChangeListener)
    }


    fun showLoadingOverlay(fm: FragmentManager, id: String) {
        if (SystemClock.elapsedRealtime() - lastAction > 1000) {
            ALog.d(TAG, "showLoadingOverlay $id dd: ${loadingRequest.size}")
            loadingRequest.add(id)
            if (!loadingDialog.isAdded && loadingRequest.size == 1) {
                ALog.d(TAG, "showLoadingOverlay showed")
                loadingDialog.show(fm, "LoadingOverLay")
            }
        }
        lastAction = SystemClock.elapsedRealtime()
    }

    fun hideLoadingOverlay(id: String) {
        ALog.d(TAG, "hideLoadingOverlay $id dd: ${loadingRequest.size}")
        loadingRequest.remove(id)
        if (loadingRequest.isEmpty()) {
            loadingDialog.dismiss()
        }
    }

    private fun setupViews() {
        val navController =
            Navigation.findNavController(this@MainActivity, R.id.navHostFragment)
        bottomNavigation?.setupWithNavController(navController)
        setSupportActionBar(topAppBar)
        supportActionBar?.title = null
        topAppBar?.setOnMenuItemClickListener { menuItem ->
            ALog.d(TAG, "search s: $menuItem")
            when (menuItem.itemId) {
                R.id.search -> {
                    val navFrag = supportFragmentManager.fragments.find { it is NavHostFragment }
                    navFrag?.childFragmentManager?.fragments?.forEach {
                        (it as? BottomNavigationAction)?.onSearch()
                    }
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
                animate().translationY(0f)
                    .alpha(1f)
                    .setDuration(200)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            super.onAnimationStart(animation)
                            isVisible = true
                        }
                    })
            } else {
                animate().translationY(-this.height.toFloat())
                    .alpha(0f)
                    .setDuration(200)
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
                if (SystemClock.elapsedRealtime() - lastAction > 3000L) {
                    showHideToolbar(false)
                }
            }, 3000)
        }
    }

    fun changeToolBarAction(fragment: Fragment) {
        when (fragment) {
            is PlayerFragment -> {
                topAppBar?.navigationIcon =
                    ResourcesCompat.getDrawable(resources, R.drawable.arrow_back, null)
                topAppBar?.setNavigationOnClickListener {
                    val navController = findNavController(R.id.navHostFragment)
                    navController.popBackStack()
                }
                topAppBar?.fitsSystemWindows = false
            }

            else -> {
                topAppBar?.navigationIcon =
                    ResourcesCompat.getDrawable(resources, R.drawable.app_logo, null)
                topAppBar?.setNavigationOnClickListener {
                    //
                }
                topAppBar?.fitsSystemWindows = true
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)

    }
}