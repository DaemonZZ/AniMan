package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.entity.SearchHistory
import com.daemonz.animange.entity.SearchHistoryData
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.addOnCompleteListener
import com.daemonz.animange.util.addOnFailureListener
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : BaseViewModel() {
    private val _searchResult = MutableLiveData<List<PagingData<Item>>>()
    val searchResult: LiveData<List<PagingData<Item>>> = _searchResult

    private val _searchData: MutableLiveData<List<SearchHistory>> = MutableLiveData(listOf())
    val searchHistoryData: LiveData<List<SearchHistory>> = _searchData

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

    fun getSearchHistory() = launchOnIO {
        repository.getSearchHistory().addOnSuccessListener { res ->
            res.toObject(SearchHistoryData::class.java)?.let {
                launchOnUI {
                    _searchData.value = it.data
                }
            }
        }.addOnFailureListener {
            launchOnUI {
                errorMessage.value = it.message
            }
        }
    }

    fun saveSearchHistory(item: Item) = launchOnIO {
        val searchData = SearchHistory(
            slug = item.slug,
            title = item.name,
            cover = item.thumbUrl,
            fullImgUrl = item.getImageUrl(imgDomain),
            timeStamp = Instant.now().epochSecond
        )
        val currentList = _searchData.value?.toMutableList() ?: mutableListOf()
        currentList.sortBy { it.timeStamp }
        if (currentList.any { it.slug == searchData.slug }) {
            currentList.removeIf { it.slug == searchData.slug }
        } else if (currentList.size >= 24) {
            currentList.removeLast()
        }
        currentList.add(0, searchData)
        repository.updateSearchHistory(currentList).addOnSuccessListener {
            ALog.d(TAG, "Update search History success")
        }.addOnFailureListener {
            ALog.e(TAG, "Update search History failed: ${it.message}")
        }
    }
}