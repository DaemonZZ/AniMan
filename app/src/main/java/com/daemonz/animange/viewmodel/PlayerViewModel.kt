package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.Episode
import com.daemonz.animange.entity.ListData
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.TypeList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(): BaseViewModel() {
    private val _playerData: MutableLiveData<ListData> = MutableLiveData()
    val playerData: LiveData<ListData> = _playerData

    private val _currentPlaying = MutableLiveData<Episode>()
    val currentPlaying: LiveData<Episode> = _currentPlaying

    private val _suggestions = MutableLiveData<ListData>()
    val suggestions: LiveData<ListData> = _suggestions

    fun loadData(item: String) {
        launchOnIO {
            val data = repository.loadPlayerData(item)
            val defaultEpisode = data.data.item?.episodes?.firstOrNull()
            withContext(Dispatchers.Main) {
                _playerData.value = data
                defaultEpisode?.let {
                    _currentPlaying.value = it.copy(
                        pivot = it.serverData.size - 1,
                    )
                }
            }
        }
    }
    fun chooseEpisode(episode: Int, server: Int = 0) {
        val data = _playerData.value
        data?.data?.item?.episodes?.let {
            val eps = if (server == 0 || server >= data.data.item.episodes.size) {
                it.firstOrNull()
            } else {
                it[server]
            }
            _currentPlaying.value = eps?.copy(
                pivot = episode
            )
        } ?: kotlin.run {
            ALog.d(TAG, "chooseEpisode: data is null")
        }
    }

    fun getSuggestions() = launchOnIO {
        //fixme need to enhance: paging it
        val cat = playerData.value?.data?.item?.category?.firstOrNull()
        cat?.let {
            val data = repository.get24RelatedFilm(
                slug = TypeList.New.value,
                category = it.slug
            )
            withContext(Dispatchers.Main) {
                _suggestions.value = data
            }

        }
    }

}