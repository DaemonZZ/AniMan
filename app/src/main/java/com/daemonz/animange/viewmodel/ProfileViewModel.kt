package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.UserType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : BaseViewModel() {
    private val _currentAvt = MutableLiveData<Int>()
    val currentAvt: LiveData<Int> = _currentAvt
    fun updateProfile(name: String, email: String, phone: String) {
        repository.updateProfile(name, email, phone)
    }
    fun updateUser(name: String? = null, image: Int? = null, userId: String) {
        repository.updateUser(name, image, userId)
    }
    fun chooseAvt(id: Int) {
        _currentAvt.value = id
    }

    fun newUser(
        name: String?,
        image: Int,
        userType: UserType = UserType.ADULT,
        password: String? = null
    ) {
        repository.newUser(name, image, userType, password)
    }

    fun switchUser(id: String) {
        repository.switchUser(id)
    }
}