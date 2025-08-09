package com.daemonz.animange.viewmodel.manga

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daemonz.animange.base.BaseViewModel
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

    private val _mangaData = MutableLiveData<ListDataManga>()
    val mangaData: LiveData<ListDataManga> = _mangaData

    private var currentManga: ListDataManga? = null

    fun getManga(slug: String) = launchOnIO {
        repository.getMangaBySlug(slug).addOnCompleteListener { listData ->
            currentManga = listData
            val lastChapter = lastestChapter(listData)
            lastChapter?.let { chap ->
                val chapterData = repository.fetchChapter(chap.url)
                launchOnUI {
                    _chapterDisplayData.value = chapterData?.data?.toChapterLinkImageList()
                }
            }
        }
    }

    private fun lastestChapter(listData: ListDataManga): ChapterDetail? {
        val chap = listData.data.item?.chapters?.lastOrNull()?.serverData?.lastOrNull()
        ALog.d(TAG, "lastestChapter: ${chap?.slug}")
        return chap
    }
}
