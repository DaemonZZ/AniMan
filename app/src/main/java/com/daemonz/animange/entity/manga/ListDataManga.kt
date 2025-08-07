package com.daemonz.animange.entity.manga

import androidx.annotation.Keep
import com.daemonz.animange.base.NetworkEntity
import com.google.gson.annotations.SerializedName

@Keep
data class ListDataManga(
    @SerializedName("data") val data: DataManga,
    @SerializedName("status") val status: String = "",
    @SerializedName("message") val message: String = ""
) : NetworkEntity()
