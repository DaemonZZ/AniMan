package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.Activity
import com.daemonz.animange.log.ALog
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor() : BaseViewModel() {

    private val _userCount = MutableLiveData(0)
    val userCount: LiveData<Int> = _userCount
    private val _userCountToday = MutableLiveData(0)
    val userCountToday: LiveData<Int> = _userCountToday
    private val _userCountYesterday = MutableLiveData(0)
    val userCountYesterday: LiveData<Int> = _userCountYesterday
    private val _userCountThisMonth = MutableLiveData(0)
    val userCountThisMonth: LiveData<Int> = _userCountThisMonth
    private val _userActive = MutableLiveData(0)
    val userActive: LiveData<Int> = _userActive
    fun getUserCount() = launchOnIO {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val today = cal.time
        cal.add(Calendar.DAY_OF_MONTH, -1)
        val yesterday = cal.time
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val beginOfMonth = cal.time
        ALog.d("tdn", "beginOfMonth: $beginOfMonth -- \n $yesterday")
        repository.getTotalUsersCount().addOnCompleteListener { task ->
            launchOnUI {
                _userCount.value = task.result.count.toInt()
            }
        }.addOnFailureListener {
            ALog.e(TAG, it.message.toString())
        }
        repository.countNewUsersFromDate(today).addOnCompleteListener { task ->
            launchOnUI {
                _userCountToday.value = task.result.count.toInt()
            }
        }.addOnFailureListener {
            ALog.e(TAG, it.message.toString())
        }
        repository.countNewUsersFromDateToDate(yesterday, today).addOnCompleteListener { task ->
            launchOnUI {
                _userCountYesterday.value = task.result.count.toInt()
            }
        }.addOnFailureListener {
            ALog.e(TAG, it.message.toString())
        }
        repository.countNewUsersFromDate(beginOfMonth).addOnCompleteListener { task ->
            launchOnUI {
                _userCountThisMonth.value = task.result.count.toInt()
            }
        }.addOnFailureListener {
            ALog.e(TAG, it.message.toString())
        }
    }
    fun getTotalUsersActiveToday() = launchOnIO {
        repository.getActiveUsersIn(24).addOnCompleteListener { task ->
            launchOnUI {
                _userActive.value = task.result.count.toInt()
            }
        }.addOnFailureListener {
            ALog.e(TAG, it.message.toString())
        }
    }
}