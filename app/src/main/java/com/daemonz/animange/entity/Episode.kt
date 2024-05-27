package com.daemonz.animange.entity

import com.google.gson.annotations.SerializedName

data class Episode(
    @SerializedName("server_name") val serverName: String = "",
    @SerializedName("server_data") val serverData: List<EpisodeDetail> = listOf(),
)
