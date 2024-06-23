package com.daemonz.animange.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.ListData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SecretViewModel @Inject constructor() : BaseViewModel() {
    private val _listDataData: MutableLiveData<ListData> = MutableLiveData()
    val listDataData: MutableLiveData<ListData> = _listDataData
    fun getSecretData() {
        launchOnIO {
            val data = repository.getDataByCategory(BuildConfig.SLUG_SECRET)
            withContext(Dispatchers.Main) {
                _listDataData.value = data
            }
        }
    }
}