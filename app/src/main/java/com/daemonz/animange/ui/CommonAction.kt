package com.daemonz.animange.ui

interface CommonAction {
    fun onBack()
}

interface MainScreenAction : CommonAction {
    fun onRefresh()
    fun onReSelectBottomNavigationItem(itemId: Int)
}

interface OnRefreshCallback {
    fun onRefreshComplete()
    fun onRefreshError()
}

interface AdBannerHandler : CommonAction {
    fun loadBanner()
    fun setupAdView()
}

interface InterstitialAdHandler : CommonAction {
    fun loadAd()
    fun showInterstitial()
}
interface NavIcon2Action : CommonAction {
    fun onNavIcon2Click()
}
