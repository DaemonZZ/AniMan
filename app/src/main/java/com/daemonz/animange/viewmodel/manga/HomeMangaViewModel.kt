package com.daemonz.animange.viewmodel.manga

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.FilmRating
import com.daemonz.animange.entity.ListData
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.addOnCompleteListener
import com.daemonz.animange.util.addOnFailureListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeMangaViewModel @Inject constructor() : BaseViewModel() {
    private val _listDataData: MutableLiveData<ListData> = MutableLiveData()
    val listDataData: MutableLiveData<ListData> = _listDataData

    private val _seriesIncoming = MutableLiveData<ListData>()
    val seriesIncoming: MutableLiveData<ListData> = _seriesIncoming

    private val _vietNamFilm = MutableLiveData<ListData>()
    val vietNamFilm: MutableLiveData<ListData> = _vietNamFilm

    private val _anime = MutableLiveData<ListData>()
    val anime: LiveData<ListData> = _anime

    private val _movies = MutableLiveData<ListData>()
    val movies: MutableLiveData<ListData> = _movies

    private val _tvShows = MutableLiveData<ListData>()
    val tvShows: MutableLiveData<ListData> = _tvShows

    fun getHomeData() {
        launchOnIO {
            repository.getHomeData().addOnCompleteListener { res ->
                if (res.data.items.isEmpty()) {
                    ALog.d(TAG, "Home data is empty")
                    return@addOnCompleteListener
                }
                ALog.d(TAG, res.data.toString())
                repository.getRatingBySlugs(res.data.items.map { it.slug }).addOnSuccessListener {
                    val rates = it.toObjects(FilmRating::class.java)
                    val data = res.data.items.map { item ->
                        item.rating =
                            rates.filter { it.slug == item.slug }.map { it.rating }.average()
                        item
                    }
                    val finalData = res.data.copy(items = data)
                    val finalListData = res.copy(data = finalData)
                    launchOnUI {
                        _listDataData.value = finalListData
                    }
                }
            }.addOnFailureListener {
                launchOnUI {
                    ALog.d(TAG, "Series Incoming slugs: ${it}")
                    errorMessage.value = it
                }
            }
        }
    }

    fun getSeriesIncoming() {
        launchOnIO {
            repository.getMangaIncoming().addOnCompleteListener { res ->
                if (res.data.items.isEmpty()) {
                    ALog.d(TAG, "Series Incoming data is empty")
                    return@addOnCompleteListener
                }
                repository.getRatingBySlugs(res.data.items.map { it.slug }).addOnSuccessListener {
                    val rates = it.toObjects(FilmRating::class.java)
                    val data = res.data.items.filter {
                        it.category.firstOrNull { it.slug == BuildConfig.SLUG_SECRET } == null
                    }.map { item ->
                        item.rating =
                            rates.filter { it.slug == item.slug }.map { it.rating }.average()
                        item
                    }
                    val finalData = res.data.copy(items = data)
                    val finalListData = res.copy(data = finalData)
                    launchOnUI {
                        _seriesIncoming.value = finalListData
                    }
                }
            }.addOnFailureListener {
                launchOnUI {
                    ALog.d(TAG, "Series Incoming slugs: ${it}")
                    errorMessage.value = it
                }
            }
        }
    }

    fun getMangaComplete() {
        launchOnIO {
            repository.getMangaComplete().addOnCompleteListener { res ->
                if (res.data.items.isEmpty()) {
                    ALog.d(TAG, "getMangaComplete data is empty")
                    return@addOnCompleteListener
                }
                repository.getRatingBySlugs(res.data.items.map { it.slug }).addOnSuccessListener {
                    val rates = it.toObjects(FilmRating::class.java)
                    val data = res.data.items.filter {
                        it.category.firstOrNull { it.slug == BuildConfig.SLUG_SECRET } == null
                    }.map { item ->
                        item.rating =
                            rates.filter { it.slug == item.slug }.map { it.rating }.average()
                        item
                    }
                    val finalData = res.data.copy(items = data)
                    val finalListData = res.copy(data = finalData)
                    launchOnUI {
                        _vietNamFilm.value = finalListData
                    }
                }
            }.addOnFailureListener {
                launchOnUI {
                    ALog.d(TAG, "getMangaComplete slugs: ${it}")
                    errorMessage.value = it
                }
            }
        }
    }

    fun getListManhwa() {
        launchOnIO {
            repository.getListManhwa().addOnCompleteListener { res ->
                if (res.data.items.isEmpty()) {
                    ALog.d(TAG, "getListManhwa data is empty")
                    return@addOnCompleteListener
                }
                repository.getRatingBySlugs(res.data.items.map { it.slug }).addOnSuccessListener {
                    val rates = it.toObjects(FilmRating::class.java)
                    val data = res.data.items.filter {
                        it.category.firstOrNull { it.slug == BuildConfig.SLUG_SECRET } == null
                    }.map { item ->
                        item.rating =
                            rates.filter { it.slug == item.slug }.map { it.rating }.average()
                        item
                    }
                    val finalData = res.data.copy(items = data)
                    val finalListData = res.copy(data = finalData)
                    launchOnUI {
                        _anime.value = finalListData
                    }
                }
            }.addOnFailureListener {
                launchOnUI {
                    ALog.d(TAG, "getListManhwa slugs: ${it}")
                    errorMessage.value = it
                }
            }
        }
    }

    fun getListManhua() {
        launchOnIO {
            repository.getListManhua().addOnCompleteListener { res ->
                if (res.data.items.isEmpty()) {
                    ALog.d(TAG, "getListManhwa data is empty")
                    return@addOnCompleteListener
                }
                repository.getRatingBySlugs(res.data.items.map { it.slug }).addOnSuccessListener {
                    val rates = it.toObjects(FilmRating::class.java)
                    val data = res.data.items.filter {
                        it.category.firstOrNull { it.slug == BuildConfig.SLUG_SECRET } == null
                    }.map { item ->
                        item.rating =
                            rates.filter { it.slug == item.slug }.map { it.rating }.average()
                        item
                    }
                    val finalData = res.data.copy(items = data)
                    val finalListData = res.copy(data = finalData)
                    launchOnUI {
                        _movies.value = finalListData
                    }
                }
            }.addOnFailureListener {
                launchOnUI {
                    errorMessage.value = it
                }
            }
        }
    }

    fun getListComingSoon() {
        launchOnIO {
            repository.getMangaComingSoon().addOnCompleteListener { res ->
                if (res.data.items.isEmpty()) {
                    ALog.d(TAG, "getMangaComingSoon data is empty")
                    return@addOnCompleteListener
                }
                repository.getRatingBySlugs(res.data.items.map { it.slug }).addOnSuccessListener {
                    val rates = it.toObjects(FilmRating::class.java)
                    val data = res.data.items.filter {
                        it.category.firstOrNull { it.slug == BuildConfig.SLUG_SECRET } == null
                    }.map { item ->
                        item.rating =
                            rates.filter { it.slug == item.slug }.map { it.rating }.average()
                        item
                    }
                    val finalData = res.data.copy(items = data)
                    val finalListData = res.copy(data = finalData)
                    launchOnUI {
                        _tvShows.value = finalListData
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