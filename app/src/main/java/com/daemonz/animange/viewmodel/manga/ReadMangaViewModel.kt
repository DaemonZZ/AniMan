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

    fun getManga(slug: String) = launchOnIO {
        repository.getMangaBySlug(slug).addOnCompleteListener { listData ->
            val lastChapter = lastestChapter(listData)
            lastChapter?.let { chap ->
                val chapterData = repository.fetchChapter(chap.url)
                ALog.d(TAG, "getManga: $chapterData")
                _chapterDisplayData.postValue(chapterData?.data?.toChapterLinkImageList())
            }
        }
    }

    private fun lastestChapter(listData: ListDataManga): ChapterDetail? {
        return listData.data.item?.chapters?.lastOrNull()?.serverData?.firstOrNull()
    }
}
