package com.daemonz.animange.entity.manga

import androidx.annotation.Keep
import com.daemonz.animange.base.NetworkEntity
import com.daemonz.animange.entity.Category
import com.daemonz.animange.entity.Country
import com.daemonz.animange.entity.Episode
import com.daemonz.animange.log.ALog
import com.daemonz.animange.util.AppMode
import com.daemonz.animange.util.AppModeEnum
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ItemManga(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String = "",
    @SerializedName("content") val content: String? = "",
    @SerializedName("origin_name") val originName: List<String> = listOf(),
    @SerializedName("type") val type: String? = "",
    @SerializedName("thumb_url") val thumbUrl: String = "",
    @SerializedName("poster_url") val posterUrl: String? = "",
    @SerializedName("time") val time: String? = "",
    @SerializedName("episode_current") val episodeCurrent: String? = "",
    @SerializedName("quality") val quality: String? = "",
    @SerializedName("lang") val language: String? = "",
    @SerializedName("year") val year: String? = "",
    @SerializedName("category") val category: List<Category> = listOf(),
    @SerializedName("country") val country: List<Country>? = listOf(),
    @SerializedName("slug") val slug: String = "",
    @SerializedName("status") val status: String = "",
    @SerializedName("episodes") val episodes: List<Episode>? = listOf(),
    @SerializedName("actor") val actor: List<String>? = listOf(),
    @SerializedName("director") val director: List<String>? = listOf(),
    @SerializedName("episode_total") val episodeTotal: String? = "",
    @SerializedName("trailer_url") val trailerUrl: String? = "",
    var rating: Double = 0.0
) : NetworkEntity(), Serializable {
    companion object {
        private const val TAG = "ItemManga"
    }

    fun getImageUrl(imgDomain: String): String {
        return if (AppMode.currentMode == AppModeEnum.Movies) "$imgDomain/uploads/movies/$thumbUrl"
        else "$imgDomain/uploads/comics/$thumbUrl"
    }
}
