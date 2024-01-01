package com.cedric.fgf.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LatestItemDao {

    @Insert
    suspend fun insert(item: LatestItem)

    @Query("DELETE FROM latest_items")
    suspend fun deleteAllLatest()

    @Query("SELECT * FROM latest_items ORDER BY id DESC")
    fun getAll(): List<LatestItem>
    @Query("SELECT * FROM latest_items WHERE id = :id")
    suspend fun getLatestItemById(id: String): LatestItem?
}