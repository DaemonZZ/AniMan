package com.daemonz.animange.entity.manga

import com.google.gson.annotations.SerializedName

data class ChapterData(
    @SerializedName("domain_cdn") var domainCdn: String? = null,
    @SerializedName("item") var item: ChapterItem? = ChapterItem()
)
