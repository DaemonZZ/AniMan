package com.daemonz.animange.util

import android.content.Context
import com.daemonz.animange.BuildConfig

class SharePreferenceManager(context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

    fun setInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}