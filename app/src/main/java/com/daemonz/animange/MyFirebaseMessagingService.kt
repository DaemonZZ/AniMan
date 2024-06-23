package com.daemonz.animange

import com.daemonz.animange.log.ALog
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "MyFirebaseMessagingServ"
    }

    override fun onNewToken(token: String) {
        ALog.d(TAG, "Refreshed token: $token")
    }

}