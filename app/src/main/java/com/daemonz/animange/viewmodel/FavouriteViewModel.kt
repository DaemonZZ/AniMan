package com.daemonz.animange.viewmodel

import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.FavouriteItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor() : BaseViewModel() {
    fun unMarkItemAsFavorite(item: FavouriteItem) {
        repository.unMarkItemAsFavourite(item)
    }

    fun markItemAsFavorite(item: FavouriteItem) {
        repository.markItemAsFavourite(item)
    }
}