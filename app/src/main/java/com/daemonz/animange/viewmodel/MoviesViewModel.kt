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
class MoviesViewModel @Inject constructor() : BaseViewModel() {
    private val _movies = MutableLiveData<List<PagingData<Item>>>()
    val movies: LiveData<List<PagingData<Item>>> = _movies

    private val cacheData: MutableMap<Int, List<Item>> = mutableMapOf()
    var imgDomain = ""
    fun getListMovies(page: Int = 1) {
        if (cacheData[page] != null) {
            _movies.value = cacheData[page]?.map {
                PagingData(
                    page = page,
                    data = it
                )
            }
            return
        }
        launchOnIO {
            repository.getListMovies(page.toString()).addOnCompleteListener { data ->
                launchOnUI {
                    imgDomain = data.data.imgDomain
                    _movies.value = data.data.items.filter {
                        it.category.firstOrNull { it.slug == BuildConfig.SLUG_SECRET } == null
                    }.map {
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
}