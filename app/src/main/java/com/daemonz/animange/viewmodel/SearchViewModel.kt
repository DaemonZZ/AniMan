package com.daemonz.animange.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.ListData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : BaseViewModel() {
    companion object {
        private const val TAG = "SearchViewModel"
    }

    private val _searchResult = MutableLiveData<ListData>()
    val searchResult: MutableLiveData<ListData> = _searchResult
    fun search(query: String) {
        launchOnIO {
            val data = repository.searchFilm(query)
            withContext(Dispatchers.Main) {
                _searchResult.value = data
            }
        }
    }
}