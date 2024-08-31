package com.daemonz.animange.entity

import androidx.annotation.Keep

@Keep
data class SearchHistoryData(
    val data: List<SearchHistory> = listOf()
)
