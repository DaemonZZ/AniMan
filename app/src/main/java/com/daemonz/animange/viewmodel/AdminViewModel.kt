package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.Activity
import com.daemonz.animange.log.ALog
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor() : BaseViewModel() {

    private val _userCount = MutableLiveData(0)
    val userCount: LiveData<Int> = _userCount
    private val _userActive = MutableLiveData(0)
    val userActive: LiveData<Int> = _userActive
    fun getUserCount() = launchOnIO {
        repository.getTotalUsersCount().addOnCompleteListener { task ->
            ALog.d(TAG, "getUserCount: ${task.result.count}")
            launchOnUI {
                _userCount.value = task.result.count.toInt()
            }
        }.addOnFailureListener {
            ALog.e(TAG, it.message.toString())
        }
    }
    fun getTotalUsersActiveToday() = launchOnIO {
        repository.getTotalUsersActiveToday().addOnCompleteListener { task ->
            val activities = task.result.toObjects(Activity::class.java)
            val activeUsers = activities.map { it.account }.toSet()
            launchOnUI {
                _userActive.value = activeUsers.size
            }
        }.addOnFailureListener {
            ALog.e(TAG, it.message.toString())
        }
    }
}