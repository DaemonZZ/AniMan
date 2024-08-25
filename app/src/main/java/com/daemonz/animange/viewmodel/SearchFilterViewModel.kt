package com.daemonz.animange.viewmodel

import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.util.Category
import com.daemonz.animange.util.Country
import com.daemonz.animange.util.TypeList
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.internal.toImmutableList
import javax.inject.Inject

@HiltViewModel
class SearchFilterViewModel @Inject constructor() : BaseViewModel() {
    val allCategories = Category.entries.toImmutableList()
    val allCountries = Country.entries.toImmutableList()
    val allTypes = TypeList.entries.toImmutableList()
}