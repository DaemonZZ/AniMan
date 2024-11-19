package com.daemonz.animange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.BuildConfig
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.Activity
import com.daemonz.animange.entity.FilmRating
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.entity.SearchHistory
import com.daemonz.animange.entity.SearchHistoryData
import com.daemonz.animange.entity.UserAction
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.Category
import com.daemonz.animange.util.Country
import com.daemonz.animange.util.LoginData
import com.daemonz.animange.util.TypeList
import com.daemonz.animange.util.Year
import com.daemonz.animange.util.addOnCompleteListener
import com.daemonz.animange.util.addOnFailureListener
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : BaseViewModel() {
    private val _searchResult = MutableLiveData<List<PagingData<Item>>>()
    val searchResult: LiveData<List<PagingData<Item>>> = _searchResult

    private val _searchData: MutableLiveData<List<SearchHistory>> = MutableLiveData(listOf())
    val searchHistoryData: LiveData<List<SearchHistory>> = _searchData

    private val cacheData: MutableMap<Int, List<Item>> = mutableMapOf()
    val allCategories = Category.entries.toList()
    val allCountries = Country.entries.toList()
    val allTypes = TypeList.entries.toList()
    val allYears = Year.entries.toList()

    var selectedType = TypeList.New
    var selectedCategory = mutableListOf(Category.All)
    var selectedYear = mutableListOf(Year.All)
    var selectedCountry = mutableListOf(Country.All)
    var imgDomain = ""
    fun search(query: String, page: Int = 0) {
        ALog.d(TAG, "Searching for $query")
        val activity = Activity(
            id = UUID.randomUUID().toString(),
            activity = UserAction.Search,
            content = "${LoginData.account?.name} searched for $query"
        )
        repository.syncActivity(activity)
        if (cacheData[page] != null) {
            _searchResult.value = cacheData[page]?.map {
                PagingData(
                    page = page,
                    data = it
                )
            }
            return
        }
        launchOnIO {
            repository.searchFilm(query, page.toString()).addOnCompleteListener { movies ->
                imgDomain = movies.data.imgDomain
                if (movies.data.items.isEmpty()) {
                    ALog.d(TAG, "No results found for $query")
                    return@addOnCompleteListener
                }
                repository.getRatingBySlugs(movies.data.items.map { it.slug })
                    .addOnSuccessListener {
                        val rates = it.toObjects(FilmRating::class.java)
                        val data = movies.data.items.filter {
                            it.category.firstOrNull { it.slug == BuildConfig.SLUG_SECRET } == null
                        }.map { item ->
                            item.rating =
                                rates.filter { it.slug == item.slug }.map { it.rating }.average()
                            PagingData(
                                page = page,
                                data = item
                            )
                        }
                        launchOnUI {
                            _searchResult.value = data
                            cacheData[page] = movies.data.items.map { item ->
                                val avg =
                                    rates.filter { it.slug == item.slug }.map { it.rating }
                                        .average()
                                item.copy(rating = avg)
                            }
                        }
                    }
                    .addOnFailureListener {
                        launchOnUI {
                            ALog.d(TAG, "Series slugs: ${it.message}")
                            errorMessage.value = it.message
                        }
                    }
            }.addOnFailureListener {
                launchOnUI {
                    errorMessage.value = it
                }
            }
        }
    }

    fun clearCache() {
        cacheData.clear()
        _searchResult.value = listOf()
    }

    fun getSearchHistory() = launchOnIO {
        repository.getSearchHistory().addOnSuccessListener { res ->
            res.toObject(SearchHistoryData::class.java)?.let { searchData ->
                if (searchData.data.isEmpty()) {
                    ALog.d(TAG, "No search history found")
                    return@addOnSuccessListener
                }
                repository.getRatingBySlugs(searchData.data.map { it.slug }).addOnSuccessListener {
                    val rates = it.toObjects(FilmRating::class.java)
                    val data = searchData.data.map { item ->
                        item.rating =
                            rates.filter { it.slug == item.slug }.map { it.rating }.average()
                        item
                    }
                    launchOnUI {
                        _searchData.value = data
                    }
                }
            }
        }.addOnFailureListener {
            launchOnUI {
                errorMessage.value = it.message
            }
        }
    }

    fun saveSearchHistory(item: Item) = launchOnIO {
        ALog.d(TAG, "Save search history: ${item.getImageUrl(imgDomain)}")
        val searchData = SearchHistory(
            slug = item.slug,
            title = item.name,
            cover = item.thumbUrl,
            fullImgUrl = item.getImageUrl(imgDomain),
            timeStamp = Instant.now().epochSecond
        )
        val currentList = _searchData.value?.toMutableList() ?: mutableListOf()
        currentList.sortBy { it.timeStamp }
        if (currentList.any { it.slug == searchData.slug }) {
            currentList.removeIf { it.slug == searchData.slug }
        } else if (currentList.size >= 24) {
            currentList.removeLast()
        }
        currentList.add(0, searchData)
        repository.updateSearchHistory(currentList).addOnSuccessListener {
            ALog.d(TAG, "Update search History success")
        }.addOnFailureListener {
            ALog.e(TAG, "Update search History failed: ${it.message}")
        }
    }
}