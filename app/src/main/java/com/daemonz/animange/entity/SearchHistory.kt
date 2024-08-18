package com.daemonz.animange.entity

import androidx.annotation.Keep

@Keep
data class SearchHistory(
    val slug: String = "",
    val title: String = "",
    val cover: String = "",
    val fullImgUrl: String = "",
    val timeStamp: Long = 0L,
    var rating: Double = 0.0
)

fun SearchHistory.toItem() = Item(
    slug = slug,
    name = title,
    thumbUrl = fullImgUrl,
    id = timeStamp.toString(), //for fun
    rating = rating
)
