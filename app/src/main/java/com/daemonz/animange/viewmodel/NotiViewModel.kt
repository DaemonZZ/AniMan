package com.daemonz.animange.viewmodel

import androidx.lifecycle.ViewModel
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.Notification
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.NotiCache
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotiViewModel @Inject constructor() : BaseViewModel() {
    fun markNotiAsRead(notification: Notification) {
        repository.markNotiAsOld(notification)
        repository.markNotiAsRead(notification).addOnSuccessListener {
            repository.getNotifications().addOnSuccessListener {
                it.toObjects(Notification::class.java).let { notificationData ->
                    NotiCache.cachedNotifications = notificationData
                }
            }.addOnFailureListener {
                ALog.e(TAG, "getNotifications: $it")
            }
        }.addOnFailureListener {
            ALog.e(TAG, "markNotiAsRead: $it")
        }
    }
}