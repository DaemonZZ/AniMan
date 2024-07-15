package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.Category
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.util.TypeList
import com.daemonz.animange.util.addOnCompleteListener
import com.daemonz.animange.util.addOnFailureListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SuggestionViewModel @Inject constructor() : BaseViewModel() {
    private val _suggestions = MutableLiveData<List<PagingData<Item>>>()
    val suggestions: LiveData<List<PagingData<Item>>> = _suggestions

    private val cacheData: MutableMap<Int, List<Item>> = mutableMapOf()
    var imgDomain = ""

    fun getSuggestions(cat: Category, page: Int) {
        if (cacheData[page] != null) {
            _suggestions.value = cacheData[page]?.map {
                PagingData(
                    page = page,
                    data = it
                )
            }
        }
        launchOnIO {
            repository.getRelatedFilms(TypeList.New.value, cat.slug, page.toString())
                .addOnCompleteListener {
                    launchOnUI {
                        imgDomain = it.data.imgDomain
                        _suggestions.value = it.data.items.map {
                            PagingData(
                                page = page,
                                data = it
                            )
                        }
                        cacheData[page] = it.data.items
                    }
                }.addOnFailureListener {
                launchOnUI {
                    errorMessage.value = it
                }
            }
        }
    }
}