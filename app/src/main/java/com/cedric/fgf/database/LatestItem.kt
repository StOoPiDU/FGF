package com.cedric.fgf.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "latest_items")
data class LatestItem(
    @PrimaryKey val id: String,
)
