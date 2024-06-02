package com.daemonz.animange.entity

import com.google.gson.annotations.SerializedName

data class SeoOnPage(
    @SerializedName("seoSchema") val seoSchema: SeoSchema? = null,
)
