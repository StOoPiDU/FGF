package com.cedric.fgf

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface FavouriteItemDao {
    @Upsert
//    suspend fun upsert(item: FavouriteItem)
    suspend fun upsert(item: FavouriteItem)

    @Delete
    suspend fun delete(item: FavouriteItem)

    @Query("SELECT * FROM favourite_items")
    fun getAll(): List<FavouriteItem>
}
