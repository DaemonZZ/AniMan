package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.FavouriteItem
import com.daemonz.animange.entity.FilmRating
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.LoginData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor() : BaseViewModel() {

    private val _favorite = MutableLiveData<List<FavouriteItem>>()
    val favorite: LiveData<List<FavouriteItem>> = _favorite

    fun unMarkItemAsFavorite(item: FavouriteItem) {
        repository.unMarkItemAsFavourite(item)
    }

    fun markItemAsFavorite(item: FavouriteItem) {
        repository.markItemAsFavourite(item)
    }
    fun getFavourite() = launchOnIO {
        ALog.d(TAG, "getFavourite: favorite")
        LoginData.getActiveUser()?.favorites?.let { favorite ->
            ALog.d(TAG, "getFavourite: favorite: $favorite")
            if (favorite.isEmpty()) {
                ALog.d(TAG, "getFavourite: favorite is empty")
                _favorite.value = emptyList()
            } else {
                repository.getRatingBySlugs(favorite.map { it.slug }).addOnSuccessListener {
                    ALog.d(TAG, "favorite: successfully")
                    val rates = it.toObjects(FilmRating::class.java)
                    val data = favorite.map { item ->
                        item.rating =
                            rates.filter { it.slug == item.slug }.map { it.rating }.average()
                        item
                    }
                    launchOnUI {
                        _favorite.value = data
                    }
                }.addOnFailureListener {
                    ALog.e(TAG, "favorite: failed")
                    launchOnUI {
                        errorMessage.value = it.message
                        _favorite.value = emptyList()
                    }
                }
            }
        } ?: launchOnUI {
            _favorite.value = emptyList()
        }
    }
}