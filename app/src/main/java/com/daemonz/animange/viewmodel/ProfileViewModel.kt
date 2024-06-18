package com.daemonz.animange.viewmodel

import com.daemonz.animange.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : BaseViewModel() {
    fun updateProfile(name: String, email: String, phone: String) {
        repository.updateProfile(name, email, phone)
    }
}