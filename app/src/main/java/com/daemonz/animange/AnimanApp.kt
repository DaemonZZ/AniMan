package com.daemonz.animange

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.daemonz.animange.util.CHANNEL_ID
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AnimanApp: Application() {
    override fun onCreate() {
        super.onCreate()
        createChannel()
    }

    private fun createChannel() {
        val name = "Animan"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance)
        notificationChannel.setSound(null, null)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}