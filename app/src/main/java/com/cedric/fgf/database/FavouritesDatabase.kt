package com.cedric.fgf.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavouriteItem::class], version = 1)
abstract class FavouritesDatabase : RoomDatabase() {
    abstract fun favouriteItemDao(): FavouriteItemDao

    companion object {
        private var INSTANCE: FavouritesDatabase? = null

        fun getInstance(context: Context): FavouritesDatabase {
            if (INSTANCE == null) {
                synchronized(FavouritesDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        FavouritesDatabase::class.java, "favourites_database"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}