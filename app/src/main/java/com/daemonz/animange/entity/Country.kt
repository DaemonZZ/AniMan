package com.daemonz.animange.entity

import com.daemonz.animange.base.NetworkEntity
import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String
): NetworkEntity()
