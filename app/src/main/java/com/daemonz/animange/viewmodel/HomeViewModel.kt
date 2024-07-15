package com.daemonz.animange.viewmodel

import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.ListData
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

    private val _allSeries = MutableLiveData<ListData>()
    val allSeries: MutableLiveData<ListData> = _allSeries
    fun getHomeData() {
        launchOnIO {
            val data = repository.getHomeData()
            withContext(Dispatchers.Main) {
                _listDataData.value = data
            }
        }
    }
    fun getSeriesIncoming() {
        launchOnIO {
            val data = repository.getSeriesInComing()
            withContext(Dispatchers.Main) {
                _seriesIncoming.value = data
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
    fun getAllSeries() {
        launchOnIO {
            val data = repository.getHomeData()
            withContext(Dispatchers.Main) {
                _allSeries.value = data
            }
        }
    }
}