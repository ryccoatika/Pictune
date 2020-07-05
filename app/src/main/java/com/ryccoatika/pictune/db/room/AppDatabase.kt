package com.ryccoatika.pictune.db.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(FavoritePhotoEntity::class), version = AppDatabase.DATABASE_VERSION)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getPhotoDao(): FavoritePhotoDao
    companion object {
        const val DATABASE_NAME = "pictune_db"
        var INSTANCE: AppDatabase? = null
        const val DATABASE_VERSION = 1

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        DATABASE_NAME
                    ).build()
                }
            }
            return INSTANCE
        }

        fun destroyDB() {
            INSTANCE = null
        }
    }
}