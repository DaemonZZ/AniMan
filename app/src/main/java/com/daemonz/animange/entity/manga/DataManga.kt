package com.daemonz.animange.entity.manga

import androidx.annotation.Keep
import com.daemonz.animange.base.NetworkEntity
import com.daemonz.animange.entity.SeoOnPage
import com.google.gson.annotations.SerializedName

@Keep
data class DataManga(
    //for showing data
    @SerializedName("items") val items: List<ItemManga> = listOf(),
    // for detail data
    @SerializedName("item") val item: ItemManga? = null,
    @SerializedName("APP_DOMAIN_CDN_IMAGE") val imgDomain: String = "",
    @SerializedName("seoOnPage") val seoOnPage: SeoOnPage? = null
) : NetworkEntity() {
    fun getListUrl(): List<String> = items.map { getImageUrl(it) }
    private fun getImageUrl(item: ItemManga): String {
        return item.getImageUrl(imgDomain)
    }

    fun getImageUrl(): String = seoOnPage?.seoSchema?.image ?: ""
}
