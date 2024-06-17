package com.daemonz.animange.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "favourite")
@Keep
data class FavouriteItem(
    @PrimaryKey val slug: String = "",
    @ColumnInfo("name") val name: String = "",
    @ColumnInfo("category") val category: List<String> = listOf(),
    @ColumnInfo("imageUrl") val imageUrl: String = "",
    @SerializedName("origin_name") val originName: String = "",
)
