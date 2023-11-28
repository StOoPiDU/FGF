package com.cedric.fgf

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_items")
data class FavouriteItem(
    @PrimaryKey val id: Int,
    val title: String
)

