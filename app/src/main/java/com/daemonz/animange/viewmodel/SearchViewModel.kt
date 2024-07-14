package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.ListData
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.addOnCompleteListener
import com.daemonz.animange.util.addOnFailureListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : BaseViewModel() {
    private val _searchResult = MutableLiveData<List<PagingData<Item>>>()
    val searchResult: LiveData<List<PagingData<Item>>> = _searchResult

    private val cacheData: MutableMap<Int, List<Item>> = mutableMapOf()
    var imgDomain = ""
    fun search(query: String, page: Int = 0) {
        ALog.d(TAG, "Searching for $query")
        if (cacheData[page] != null) {
            _searchResult.value = cacheData[page]?.map {
                PagingData(
                    page = page,
                    data = it
                )
            }
            return
        }
        launchOnIO {
            repository.searchFilm(query, page.toString()).addOnCompleteListener { data ->
                launchOnUI {
                    if (data.data.items.isEmpty()) {
                        return@launchOnUI
                    }
                    imgDomain = data.data.imgDomain
                    _searchResult.value = data.data.items.map {
                        PagingData(
                            page = page,
                            data = it
                        )
                    }
                    cacheData[page] = data.data.items
                }
            }.addOnFailureListener {
                launchOnUI {
                    errorMessage.value = it
                }
            }
        }
    }

    fun clearCache() {
        cacheData.clear()
        _searchResult.value = listOf()
    }
}