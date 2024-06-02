package com.daemonz.animange.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite")
data class FavouriteItem(
    @PrimaryKey val slug: String,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("category") val category: List<String>,
    @ColumnInfo("imageUrl") val imageUrl: String,
)
