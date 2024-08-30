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

interface AdmobHandler : CommonAction {
    fun loadBanner()
    fun setupAdView()
}
