package com.daemonz.animange.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.Home
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): BaseViewModel() {
    private val _homeData: MutableLiveData<Home> = MutableLiveData()
    val homeData: MutableLiveData<Home> = _homeData
    fun getHomeData() {
        launchOnIO {
            val data = repository.getHomeData()
            withContext(Dispatchers.Main) {
                _homeData.value = data
            }
        }
    }
}