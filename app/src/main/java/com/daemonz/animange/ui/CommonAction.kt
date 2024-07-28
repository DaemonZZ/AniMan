package com.daemonz.animange.ui

import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.entity.Item

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

interface BottomNavigationAction : MainScreenAction {
    fun onSearch()
}