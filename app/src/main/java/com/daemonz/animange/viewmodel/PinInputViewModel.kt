package com.daemonz.animange.viewmodel

import com.daemonz.animange.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PinInputViewModel @Inject constructor() : BaseViewModel() {
    fun switchUser(id: String) {
        repository.switchUser(id)
    }
}