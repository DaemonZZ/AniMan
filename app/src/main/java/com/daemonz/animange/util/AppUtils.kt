package com.daemonz.animange.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.daemonz.animange.log.ALog

object AppUtils {
    private const val TAG = "AppUtils"
    fun playYoutube(context: Context, url: String) {
        ALog.d(TAG, "playYoutube: $url")
        val intent = Intent(Intent.ACTION_VIEW, url.toYoutubeUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun String.toYoutubeUri(): Uri {
        val id = this.substringAfter("v=")
        return Uri.parse("vnd.youtube:$id")
    }
}