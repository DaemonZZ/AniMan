package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.log.ALog
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor() : BaseViewModel() {

    private val _userCount = MutableLiveData(0)
    val userCount: LiveData<Int> = _userCount
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
}