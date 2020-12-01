package com.ryccoatika.core.data.source.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ryccoatika.core.data.source.local.entity.PhotoHistoryEntity
import com.ryccoatika.core.data.source.local.entity.PhotoMinimalEntity

@Database(entities = [PhotoMinimalEntity::class, PhotoHistoryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun getFavoriteDao(): FavoriteDao
    
    abstract fun getHistoryDao(): HistoryDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "pictune.db").build()
                instance = newInstance
                newInstance
            }
        fun destroyDatabase() {
            instance = null
        }
    }
}