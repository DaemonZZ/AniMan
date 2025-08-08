package com.daemonz.animange.entity.manga

import com.google.gson.annotations.SerializedName

data class ChapterApiResponse(
    @SerializedName("status") var status: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ChapterData? = ChapterData()
)
