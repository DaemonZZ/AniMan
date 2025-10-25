package com.daemonz.animange.viewmodel.manga

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
import com.daemonz.animange.entity.Category
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.PagingData
import com.daemonz.animange.entity.manga.Chapter
import com.daemonz.animange.entity.manga.ChapterApiResponse
import com.daemonz.animange.entity.manga.ChapterDetail
import com.daemonz.animange.entity.manga.ChapterLinkImageList
import com.daemonz.animange.entity.manga.ListDataManga
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.addOnCompleteListener
import com.daemonz.animange.util.toChapterLinkImageList
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReadMangaViewModel @Inject constructor() : BaseViewModel() {

    private val _chapterDisplayData = MutableLiveData<ChapterLinkImageList>()
    val chapterDisplayData: LiveData<ChapterLinkImageList> = _chapterDisplayData

    private val _suggestion = MutableLiveData<List<PagingData<Item>>>()
    val suggestion: LiveData<List<PagingData<Item>>> = _suggestion

    private val _currentChapter = MutableLiveData<Chapter>()
    val currentChapter: LiveData<Chapter> = _currentChapter


    var currentManga: ListDataManga? = null
        private set
    private val listChapter = mutableListOf<ChapterApiResponse>()

    private var pickedCat: Category? = null
    private val cacheSuggestions = mutableMapOf<Int, List<Item>>()
    var imgDomain = ""

    fun getManga(slug: String) = launchOnIO {
        repository.getMangaBySlug(slug).addOnCompleteListener { listData ->
            currentManga = listData
            getSuggestions(1)
            val lastChapter = lastestChapter(listData)
            lastChapter?.let { chap ->
                val chapterData = repository.fetchChapter(chap.url)
                launchOnUI {
                    _chapterDisplayData.value = chapterData?.data?.toChapterLinkImageList()
                    val defaultChapter = listData.data.item?.chapters?.lastOrNull()
                    defaultChapter?.let {
                        _currentChapter.value =
                            it.copy(pivot = defaultChapter.serverData.lastOrNull()?.slug.toString())
                    }
                }
            }
        }
    }

    private fun lastestChapter(listData: ListDataManga): ChapterDetail? {
        val chap = listData.data.item?.chapters?.lastOrNull()?.serverData?.lastOrNull()
        ALog.d(TAG, "lastestChapter: ${chap?.slug}")
        return chap
    }
    fun updateChapter(index: Int) {
        val chap = currentManga?.data?.item?.chapters?.lastOrNull()?.serverData?.get(index)
        ALog.d(TAG, "updateChapter: ${chap?.slug}")
        chap?.let { chap ->
            val chapterData = repository.fetchChapter(chap.url)
            launchOnUI {
                _chapterDisplayData.value = chapterData?.data?.toChapterLinkImageList()
            }
        }
    }

    fun getSuggestions(page: Int) {
        ALog.d(TAG, "getSuggestions: $page")
        if (page == 1) {
            pickedCat = currentManga?.data?.item?.category?.random()
            cacheSuggestions.clear()
        }
        pickedCat?.let {
            launchOnIO {
                repository.getMangeByCategory(it.slug, page).addOnCompleteListener { data ->
                    imgDomain = data.data.imgDomain
                    launchOnUI {
                        _suggestion.value = data.data.items.map {
                            PagingData(
                                page = page,
                                data = it
                            )
                        }
                    }
                    cacheSuggestions[page] = data.data.items
                }
            }
        }
    }

    //Chua can dung
    private fun fetchAllChapter(listData: ListDataManga) {
        launchOnIO {
            listChapter.clear()
            currentManga?.data?.item?.chapters?.firstOrNull()?.serverData?.forEach { chap ->
                repository.fetchChapter(chap.url)?.let {
                    listChapter.add(it)
                }
            }
        }
    }
}
