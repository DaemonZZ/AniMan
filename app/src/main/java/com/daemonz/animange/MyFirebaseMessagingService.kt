package com.daemonz.animange

import com.daemonz.animange.log.ALog
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "MyFirebaseMessagingServ"
    }

    override fun onNewToken(token: String) {
        ALog.d(TAG, "Refreshed token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        ALog.d(TAG, "From: ${message.from} - message: ${message.data}")
    }
}