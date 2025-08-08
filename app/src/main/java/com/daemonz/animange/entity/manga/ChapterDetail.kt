package com.daemonz.animange.entity.manga

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ChapterDetail(
    @SerializedName("chapter_title") val name: String = "",
    @SerializedName("chapter_name") val slug: String = "",
    @SerializedName("filename") val filename: String = "",
    @SerializedName("chapter_api_data") val url: String = "",
)
