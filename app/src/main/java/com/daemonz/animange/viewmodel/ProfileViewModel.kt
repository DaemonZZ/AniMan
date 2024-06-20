package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : BaseViewModel() {
    private val _currentAvt = MutableLiveData<Int>()
    val currentAvt: LiveData<Int> = _currentAvt
    fun updateProfile(name: String, email: String, phone: String) {
        repository.updateProfile(name, email, phone)
    }
    fun chooseAvt(id: Int) {
        _currentAvt.value = id
    }
}