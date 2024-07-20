package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.ListData
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.util.addOnCompleteListener
import com.daemonz.animange.util.addOnFailureListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SeriesViewModel @Inject constructor() : BaseViewModel() {
    private val _series = MutableLiveData<List<PagingData<Item>>>()
    val series: LiveData<List<PagingData<Item>>> = _series

    private val cacheData: MutableMap<Int, List<Item>> = mutableMapOf()
    var imgDomain = ""

    fun getAllSeries(page: Int) {
        if (cacheData[page] != null) {
            _series.value = cacheData[page]?.map {
                PagingData(
                    page = page,
                    data = it
                )
            }
        }
        launchOnIO {
            repository.getAllSeries(page.toString()).addOnCompleteListener {
                launchOnUI {
                    imgDomain = it.data.imgDomain
                    _series.value = it.data.items.filter {
                        it.category.firstOrNull { it.slug == BuildConfig.SLUG_SECRET } == null
                    }.map {
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