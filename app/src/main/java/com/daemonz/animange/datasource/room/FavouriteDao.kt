package com.daemonz.animange.datasource.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.daemonz.animange.entity.FavouriteItem

@Dao
interface FavouriteDao {
    @Query("SELECT * FROM favourite")
    fun getAll(): List<FavouriteItem>

    @Query("SELECT * FROM favourite WHERE slug IN (:slug)")
    fun loadAllBySlug(slug: String): List<FavouriteItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: FavouriteItem)

    @Query("DELETE FROM favourite WHERE slug = (:slug) ")
    fun delete(slug: String)

    @Update
    fun update(item: FavouriteItem)

}