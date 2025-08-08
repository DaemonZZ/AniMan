package com.daemonz.animange.entity.manga

import androidx.annotation.Keep
import com.daemonz.animange.entity.EpisodeDetail
import com.google.gson.annotations.SerializedName

@Keep
data class Chapter(
    @SerializedName("server_name") val serverName: String = "",
    @SerializedName("server_data") val serverData: List<ChapterDetail> = listOf(),
    val pivot: String = "",
)
