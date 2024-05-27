package com.daemonz.animange.entity

import com.daemonz.animange.base.NetworkEntity
import com.daemonz.animange.log.ALog
import com.google.gson.annotations.SerializedName

data class Data(
    val items: List<Item>,
    //  /uploads/movies/
    @SerializedName("APP_DOMAIN_CDN_IMAGE") val imgDomain: String = "",
) : NetworkEntity() {
    fun getListUrl(): List<String> = items.map { getImageUrl(it) }
    private fun getImageUrl(item:Item): String {
        ALog.i("Thangdn6", "getImageUrl: ${item.thumbUrl}")
        return "$imgDomain/uploads/movies/${item.thumbUrl}"
    }
}
