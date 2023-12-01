package com.cedric.fgf

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface FavouriteItemDao {
    @Upsert
    suspend fun upsert(item: FavouriteItem)

    @Delete
    suspend fun delete(item: FavouriteItem)

    @Query("SELECT * FROM favourite_items ORDER BY id DESC")
    fun getAll(): List<FavouriteItem>

    @Query("DELETE FROM favourite_items")
    suspend fun deleteAll()

    @Query("SELECT * FROM favourite_items WHERE id = :id")
    suspend fun getItemById(id: String): FavouriteItem?
}
