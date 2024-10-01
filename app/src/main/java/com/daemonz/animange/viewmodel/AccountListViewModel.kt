package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.Account
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountListViewModel @Inject constructor() : BaseViewModel() {
    private val _accounts = MutableLiveData<List<Account>>()
    val accounts: LiveData<List<Account>> = _accounts
    fun getAccount() = launchOnIO {
        repository.getListAccounts().addOnSuccessListener { col ->
            col.toObjects(Account::class.java).let {
                launchOnUI {
                    _accounts.value = it
                }
            }
        }.addOnFailureListener {
            launchOnUI {
                errorMessage.value = it.message
            }
        }
    }
}