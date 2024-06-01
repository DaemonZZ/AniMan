package com.daemonz.animange.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daemonz.animange.repo.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var repository: DataRepository

    @Inject
    lateinit var ioScope: CoroutineScope
    protected val TAG = this::class.java.simpleName
    var errorMessage = MutableLiveData<String?>()
    fun launchOnUI(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch {
            try {
                block()
            } catch (ex: UnknownHostException) {
                errorMessage.postValue("Check your internet connection and try again !")
            } catch (ex: Exception) {
                errorMessage.postValue("ERROR ${ex.message}")
            }
        }
    }

    fun launchOnIO(block: suspend CoroutineScope.() -> Unit): Job {
        return ioScope.launch {
            try {
                block()
            } catch (ex: UnknownHostException) {
                errorMessage.postValue("launchOnIO Check your internet connection and try again !")
            } catch (ex: Exception) {
                ex.printStackTrace()
                errorMessage.postValue("launchOnIO ERROR ${ex.message}")
            }
        }
    }

    fun <T> asyncOnIO(block: suspend CoroutineScope.() -> T): Deferred<T> =
        ioScope.async { block() }


    suspend fun <T> asyncOnIOAwait(block: suspend CoroutineScope.() -> T): T =
        asyncOnIO(block).await()


}