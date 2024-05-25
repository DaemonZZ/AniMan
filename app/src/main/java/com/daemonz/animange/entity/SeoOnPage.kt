package com.daemonz.animange.entity

import com.daemonz.animange.BuildConfig
import com.google.gson.annotations.SerializedName

data class SeoOnPage(
    @SerializedName("og_image") val ogImage: List<String>,
) {
    fun getListUrl(): List<String> {
        return ogImage.map {
            "${BuildConfig.IMG_BASE_URL}$it"
        }
    }
}
