package com.cedric.fgf.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LatestItem::class], version = 1)

abstract class LatestDatabase : RoomDatabase() {
    abstract fun latestItemDao(): LatestItemDao

    companion object {
        private var INSTANCE: LatestDatabase? = null

        fun getInstance(context: Context): LatestDatabase {
            if (INSTANCE == null) {
                synchronized(FavouritesDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        LatestDatabase::class.java, "latest_database"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}