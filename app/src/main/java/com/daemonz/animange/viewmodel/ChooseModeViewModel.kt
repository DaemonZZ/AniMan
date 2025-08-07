package com.daemonz.animange.viewmodel

import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.util.AppMode
import com.daemonz.animange.util.AppModeEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChooseModeViewModel @Inject constructor() : BaseViewModel() {
    fun chooseMode(mode: AppModeEnum) {
        AppMode.currentMode = mode
    }
}