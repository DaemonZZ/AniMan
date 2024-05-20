package com.daemonz.animange.entity

import com.daemonz.animange.base.NetworkEntity
import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("origin_name") val originName: String,
    @SerializedName("type") val type: String,
    @SerializedName("thumb_url") val thumbUrl: String,
    @SerializedName("time") val time: String,
    @SerializedName("episode_current") val episodeCurrent: String,
    @SerializedName("quality") val quality: String,
    @SerializedName("lang") val language: String,
    @SerializedName("year") val year: String,
    @SerializedName("category") val category: List<Category>,
    @SerializedName("country") val country: List<Country>,
):NetworkEntity()
