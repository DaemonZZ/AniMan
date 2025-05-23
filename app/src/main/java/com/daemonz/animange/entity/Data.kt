package com.daemonz.animange.entity

import androidx.annotation.Keep
import com.daemonz.animange.base.NetworkEntity
import com.google.gson.annotations.SerializedName

@Keep
data class Data(
    //for showing data
    @SerializedName("items") val items: List<Item> = listOf(),
    // for detail data
    @SerializedName("item") val item: Item? = null,
    @SerializedName("APP_DOMAIN_CDN_IMAGE") val imgDomain: String = "",
    @SerializedName("seoOnPage") val seoOnPage: SeoOnPage? = null
) : NetworkEntity() {
    fun getListUrl(): List<String> = items.map { getImageUrl(it) }
    private fun getImageUrl(item: Item): String {
        return item.getImageUrl(imgDomain)
    }

    fun getImageUrl(): String = seoOnPage?.seoSchema?.image ?: ""
}
