package com.ryccoatika.core.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ryccoatika.core.data.source.local.entity.PhotoHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photoHistoryEntity: PhotoHistoryEntity)

    @Query("SELECT * FROM photohistoryentity")
    fun getAllPhoto(): Flow<List<PhotoHistoryEntity>>

    @Query("DELETE FROM photohistoryentity")
    suspend fun deleteAllPhoto()
}