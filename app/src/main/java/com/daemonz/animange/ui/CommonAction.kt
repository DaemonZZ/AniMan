package com.daemonz.animange.ui

import com.daemonz.animange.base.OnItemClickListener
import com.daemonz.animange.entity.Item

interface CommonAction {
    fun onRefresh()
    fun onReSelectBottomNavigationItem(itemId: Int)
}

interface OnRefreshCallback {
    fun onRefreshComplete()
    fun onRefreshError()
}

interface BottomNavigationAction : CommonAction {
    fun onSearch()
}