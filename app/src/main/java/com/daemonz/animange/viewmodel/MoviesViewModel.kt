package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.FilmRating
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.ListData
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.log.ALog
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
            repository.getListMovies(page.toString()).addOnCompleteListener { movies ->
                imgDomain = movies.data.imgDomain
                repository.getRatingBySlugs(movies.data.items.map { it.slug })
                    .addOnSuccessListener {
                        val rates = it.toObjects(FilmRating::class.java)
                        val data = movies.data.items.filter {
                            it.category.firstOrNull { it.slug == BuildConfig.SLUG_SECRET } == null
                        }.map { item ->
                            item.rating =
                                rates.filter { it.slug == item.slug }.map { it.rating }.average()
                            ALog.d(TAG, "Series slugs: ${item.rating}")
                            PagingData(
                                page = page,
                                data = item
                            )
                        }
                        launchOnUI {
                            _movies.value = data
                            cacheData[page] = movies.data.items.map { item ->
                                val avg =
                                    rates.filter { it.slug == item.slug }.map { it.rating }
                                        .average()
                                item.copy(rating = avg)
                            }
                        }
                    }.addOnFailureListener {
                        launchOnUI {
                            ALog.d(TAG, "Series slugs: ${it.message}")
                            errorMessage.value = it.message
                        }
                    }

            }.addOnFailureListener {
                launchOnUI {
                    errorMessage.value = it
                }
            }
        }
    }
}