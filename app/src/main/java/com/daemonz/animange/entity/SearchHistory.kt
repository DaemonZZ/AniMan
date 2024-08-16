package com.daemonz.animange.entity

import androidx.annotation.Keep

@Keep
data class SearchHistory(
    val slug: String = "",
    val title: String = "",
    val cover: String = "",
    val fullImgUrl: String = "",
    val timeStamp: Long = 0L
)

fun SearchHistory.toItem() = Item(
    slug = slug,
    name = title,
    thumbUrl = fullImgUrl,
    id = timeStamp.toString() //for fun
)
