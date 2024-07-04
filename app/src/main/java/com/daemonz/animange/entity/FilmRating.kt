package com.daemonz.animange.entity

import androidx.annotation.Keep

@Keep
data class FilmRating(
    val id: String = "",
    val slug: String = "",
//    val film: Item? = null,
    val rating: Double = 0.0,
    val comment: String = "",
    val user: User? = null,
)
