package com.daemonz.animange.ui

interface CommonAction {
    fun onRefresh()
    fun onReSelectBottomNavigationItem(itemId: Int)
}

interface OnRefreshCallback {
    fun onRefreshComplete()
    fun onRefreshError()
}