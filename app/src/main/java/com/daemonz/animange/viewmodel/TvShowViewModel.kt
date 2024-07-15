package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.util.addOnCompleteListener
import com.daemonz.animange.util.addOnFailureListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TvShowViewModel @Inject constructor() : BaseViewModel() {
    private val _shows = MutableLiveData<List<PagingData<Item>>>()
    val shows: LiveData<List<PagingData<Item>>> = _shows

    private val cacheData: MutableMap<Int, List<Item>> = mutableMapOf()
    var imgDomain = ""

    fun getTvShows(page: Int) {
        if (cacheData[page] != null) {
            _shows.value = cacheData[page]?.map {
                PagingData(
                    page = page,
                    data = it
                )
            }
        }
        launchOnIO {
            repository.getTvShows(page.toString()).addOnCompleteListener {
                launchOnUI {
                    imgDomain = it.data.imgDomain
                    _shows.value = it.data.items.map {
                        PagingData(
                            page = page,
                            data = it
                        )
                    }
                    cacheData[page] = it.data.items
                }
            }.addOnFailureListener {
                launchOnUI {
                    errorMessage.value = it
                }
            }
        }
    }
}