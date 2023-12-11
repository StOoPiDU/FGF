package com.cedric.fgf.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_items")
data class FavouriteItem(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val url: String,
//    val link_flair_css_class: String,
    val thumbnail: String
)

