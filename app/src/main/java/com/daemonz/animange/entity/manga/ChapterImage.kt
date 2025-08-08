package com.daemonz.animange.entity.manga

import com.google.gson.annotations.SerializedName

data class ChapterImage(
    @SerializedName("image_page") var imagePage: Int? = null,
    @SerializedName("image_file") var imageFile: String? = null
)
