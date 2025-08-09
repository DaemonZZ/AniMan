package com.daemonz.animange.entity.manga

data class ChapterLinkImageList(
    val name: String?,
    val chapterName: String?,
    val imageList: List<ImagePage>
)

data class ImagePage(
    val name: Int = -1,
    val url: String = ""
)
