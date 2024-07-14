package com.daemonz.animange.entity

import androidx.annotation.Keep

@Keep
data class PagingData<T>(
    val page: Int,
    val data: T
)
