package com.daemonz.animange.entity.manga

import com.google.gson.annotations.SerializedName

data class ChapterItem(
    @SerializedName("_id") var Id: String? = null,
    @SerializedName("comic_name") var comicName: String? = null,
    @SerializedName("chapter_name") var chapterName: String? = null,
    @SerializedName("chapter_title") var chapterTitle: String? = null,
    @SerializedName("chapter_path") var chapterPath: String? = null,
    @SerializedName("chapter_image") var chapterImage: ArrayList<ChapterImage> = arrayListOf()
)
