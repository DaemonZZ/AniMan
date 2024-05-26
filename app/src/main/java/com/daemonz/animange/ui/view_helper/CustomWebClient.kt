package com.daemonz.animange.ui.view_helper

import android.os.Message
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.daemonz.animange.log.ALog

class CustomWebClient(
    private val showWebView: ()-> Unit,
    private val hideWebView: (View?)-> Unit,
    private val addView: (View?)-> Unit,
):WebChromeClient() {
    companion object {
        private const val TAG = "CustomWebClient"
    }
    private var fullScreen: View? = null
    override fun onHideCustomView() {
        ALog.i(TAG, "onHideCustomView: ")
        fullScreen?.visibility = View.GONE
        showWebView.invoke()
    }


    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        ALog.i(TAG, "onShowCustomView:")
        hideWebView.invoke(fullScreen)
        fullScreen = view
        addView.invoke(fullScreen)
        fullScreen?.visibility = View.VISIBLE
    }
}