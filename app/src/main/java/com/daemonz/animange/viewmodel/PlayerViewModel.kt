package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.Episode
import com.daemonz.animange.entity.EpisodeDetail
import com.daemonz.animange.entity.ListData
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

}