package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.Account
import com.daemonz.animange.entity.Activity
import com.daemonz.animange.log.ALog
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor() : BaseViewModel() {
    private val _activities = MutableLiveData<List<Activity>>()
    val activities: LiveData<List<Activity>> = _activities

    private val _accounts = MutableLiveData<List<Account>>()
    val accounts: LiveData<List<Account>> = _accounts

    fun getActiveUsersIn(h: Int) = launchOnIO {
        repository.getListActiveUsersIn(24).addOnCompleteListener { task ->
            val accounts = task.result.toObjects(Account::class.java)
            launchOnUI {
                _accounts.value = accounts
            }
        }
    }

    fun getTotalUsersActiveToday() = launchOnIO {
        repository.getTotalUsersActiveToday().addOnCompleteListener { task ->
            val activities = task.result.toObjects(Activity::class.java)
            launchOnUI {
                _activities.value = activities
            }
        }.addOnFailureListener {
            ALog.e(TAG, it.message.toString())
        }
    }
}