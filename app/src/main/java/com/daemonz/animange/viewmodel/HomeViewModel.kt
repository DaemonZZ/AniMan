package com.daemonz.animange.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.FilmRating
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
class HomeViewModel @Inject constructor(): BaseViewModel() {
    private val _listDataData: MutableLiveData<ListData> = MutableLiveData()
    val listDataData: MutableLiveData<ListData> = _listDataData

    private val _seriesIncoming = MutableLiveData<ListData>()
    val seriesIncoming: MutableLiveData<ListData> = _seriesIncoming

    private val _vietNamFilm = MutableLiveData<ListData>()
    val vietNamFilm: MutableLiveData<ListData> = _vietNamFilm

    private val _anime = MutableLiveData<ListData>()
    val anime: MutableLiveData<ListData> = _anime

    private val _movies = MutableLiveData<ListData>()
    val movies: MutableLiveData<ListData> = _movies

    private val _tvShows = MutableLiveData<ListData>()
    val tvShows: MutableLiveData<ListData> = _tvShows

    fun getHomeData() {
        launchOnIO {
            repository.getHomeData().addOnCompleteListener { res ->
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
            repository.getSeriesInComing().addOnCompleteListener { res ->
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

    fun getListFilmVietNam() {
        launchOnIO {
            val data = repository.getListFilmVietNam()
            withContext(Dispatchers.Main) {
                _vietNamFilm.value = data
            }
        }
    }
    fun getListAnime() {
        launchOnIO {
            val data = repository.getListAnime()
            withContext(Dispatchers.Main) {
                anime.value = data
            }
        }
    }

    fun getListMovies() {
        launchOnIO {
            repository.getListMovies().addOnCompleteListener { data ->
                launchOnUI {
                    _movies.value = data
                }
            }.addOnFailureListener {
                launchOnUI {
                    errorMessage.value = it
                }
            }
        }
    }
    fun getTvShows() {
        launchOnIO {
            repository.getTvShows("").addOnCompleteListener {
                launchOnUI {
                    _tvShows.value = it
                }
            }.addOnFailureListener {
                launchOnUI {
                    errorMessage.value = it
                }
            }

        }
    }
}