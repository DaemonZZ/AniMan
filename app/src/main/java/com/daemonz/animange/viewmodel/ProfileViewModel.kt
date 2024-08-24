package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.UserType
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.LoginData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : BaseViewModel() {
    private val _currentAvt = MutableLiveData<Int>()
    val currentAvt: LiveData<Int> = _currentAvt
    fun updateProfile(name: String, email: String, phone: String) {
        repository.updateProfile(name, email, phone)
    }

    fun updateUser(name: String? = null, password: String?, image: Int? = null, userId: String) {
        repository.updateUser(name, password, image, userId)
    }

    fun chooseAvt(id: Int) {
        _currentAvt.value = id
    }

    fun newUser(
        name: String?,
        password: String? = null,
        image: Int,
        userType: UserType = UserType.ADULT,
    ) {
        repository.newUser(name, image, userType, password)
    }

    fun switchUser(id: String) {
        repository.switchUser(id)
    }

    fun deleteUser(id: String) {
        val listUser = LoginData.account?.users?.toMutableList()
        listUser?.removeIf { it.id == id }
        listUser?.let {
            LoginData.account?.users = it
        }
        LoginData.account?.let {
            repository.updateAccount(it).addOnSuccessListener {
                otherMessage.postValue("Delete user success")
            }
        }
    }
}