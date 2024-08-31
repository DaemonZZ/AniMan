package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.FilmRating
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.log.ALog
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
            repository.getTvShows(page.toString()).addOnCompleteListener { shows ->
                imgDomain = shows.data.imgDomain
                if (shows.data.items.isEmpty()) {
                    ALog.e(TAG, "Tv show is empty")
                    return@addOnCompleteListener
                }
                repository.getRatingBySlugs(shows.data.items.map { it.slug })
                    .addOnSuccessListener {
                        val rates = it.toObjects(FilmRating::class.java)
                        val data = shows.data.items.filter {
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
                            _shows.value = data
                            cacheData[page] = shows.data.items.map { item ->
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