package com.daemonz.animange.entity

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Episode(
    @SerializedName("server_name") val serverName: String = "",
    @SerializedName("server_data") val serverData: List<EpisodeDetail> = listOf(),
    val pivot: Int = 0,
) {
    fun getCurrentEpisodeDetail(): EpisodeDetail {
        return serverData[pivot]
    }
}
