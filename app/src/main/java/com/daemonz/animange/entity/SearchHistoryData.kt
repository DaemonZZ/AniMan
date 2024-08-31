package com.daemonz.animange.entity

import androidx.annotation.Keep

@Keep
data class SearchHistoryData(
    var data: List<SearchHistory> = listOf()
)
