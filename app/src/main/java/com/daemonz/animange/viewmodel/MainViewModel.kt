package com.daemonz.animange.viewmodel

import com.daemonz.animange.log.ALog
import com.daemonz.animange.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }
  fun getHome() = launchOnIO {
      val data = repository.getHomeData()
      withContext(Dispatchers.Main) {
           //TODO
          ALog.d(TAG, "data = $data" )
      }
  }
}