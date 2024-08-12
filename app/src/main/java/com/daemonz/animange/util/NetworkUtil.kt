package com.daemonz.animange.util

import android.content.Context
import android.net.ConnectivityManager


object NetworkUtil {
    const val TYPE_WIFI: Int = 1
    const val TYPE_MOBILE: Int = 2
    const val TYPE_NOT_CONNECTED: Int = 0


    fun getConnectivityStatus(context: Context): Int {
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo
        if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI

            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE
        }
        return TYPE_NOT_CONNECTED
    }

    fun getConnectivityStatusString(context: Context): String? {
        val conn = getConnectivityStatus(context)
        var status: String? = null
        if (conn == TYPE_WIFI) {
            status = "Wifi enabled"
        } else if (conn == TYPE_MOBILE) {
            status = "Mobile data enabled"
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet"
        }
        return status
    }
}