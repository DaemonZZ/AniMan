package com.daemonz.animange.ui.custom

import android.content.Context
import android.webkit.WebView
import com.daemonz.animange.log.ALog

class DzWebView(context: Context) : WebView(context) {
    companion object {
        private const val TAG = "DzWebView"
    }

    var isAttached: Boolean = false
        private set

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ALog.d(TAG, "onAttachedToWindow")
        isAttached = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        ALog.d(TAG, "onDetachedFromWindow")
        isAttached = false
    }
}