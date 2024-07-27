package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.FeedBack
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SupportViewModel @Inject constructor() : BaseViewModel() {
    private val _feedback = MutableLiveData<Boolean?>(null)
    val feedback: LiveData<Boolean?> = _feedback
    fun sendFeedback(feedBack: FeedBack) {
        repository.sendFeedBack(feedBack).addOnSuccessListener {
            _feedback.value = true
        }.addOnFailureListener {
            _feedback.value = false
        }
    }
}