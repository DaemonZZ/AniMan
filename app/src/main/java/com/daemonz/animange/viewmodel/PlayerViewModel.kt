package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.Episode
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.ListData
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.LoginData
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

    private val _isFavourite = MutableLiveData<Boolean>()
    val isFavourite: LiveData<Boolean> = _isFavourite

    fun loadData(item: String) {
        launchOnIO {
            val data = repository.loadPlayerData(item)
            val defaultEpisode = data.data.item?.episodes?.firstOrNull()
            withContext(Dispatchers.Main) {
                _playerData.value = data
                isItemFavourite()
                defaultEpisode?.let {
                    _currentPlaying.value = it.copy(
                        pivot = 0,
                    )
                }
            }
        }
    }
    fun chooseEpisode(episode: Int, server: Int = 0) {
        if (episode == currentPlaying.value?.pivot && playerData.value?.data?.item?.episodes?.get(
                server
            )?.serverName == currentPlaying.value?.serverName
        ) {
            ALog.i(TAG, "Same Episode selected")
            return
        }
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
        //FIXME fixme need to enhance: paging it
        val cat = playerData.value?.data?.item?.category?.random()
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

    fun markItemAsFavorite(item: Item? = null) = launchOnIO {
        ALog.d(TAG, "markItemAsFavourite: $item")
        if (item == null) {
            playerData.value?.data?.let {
                if (it.item != null) {
                    repository.markItemAsFavourite(item = it.item, img = it.getImageUrl())
                    withContext(Dispatchers.Main) {
                        _isFavourite.value = true
                    }
                }
            }
        } else {
            repository.markItemAsFavourite(
                item = item,
                item.getImageUrl(suggestions.value?.data?.imgDomain.toString())
            )
        }

    }

    fun unMarkItemAsFavorite(item: Item? = null) = launchOnIO {
        ALog.d(TAG, "unMarkItemAsFavourite: $item")
        if (item == null) {
            playerData.value?.data?.let {
                if (it.item != null) {
                    repository.unMarkItemAsFavourite(it.item)
                    withContext(Dispatchers.Main) {
                        _isFavourite.value = false
                    }
                }
            }
        } else {
            repository.unMarkItemAsFavourite(item)
        }

    }

    private suspend fun isItemFavourite() = launchOnIO {
        val res =
            playerData.value?.data?.item?.slug?.let {
                LoginData.getActiveUser()?.isFavourite(it)
            } ?: false
        withContext(Dispatchers.Main) {
            _isFavourite.value = res
        }
    }
}