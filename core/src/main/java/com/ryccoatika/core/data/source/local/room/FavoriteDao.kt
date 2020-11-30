package com.ryccoatika.core.data.source.local.room

import androidx.room.*
import com.ryccoatika.core.data.source.local.entity.PhotoMinimalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoMinimal(photo: PhotoMinimalEntity)

    @Delete
    suspend fun deletePhotoMinimal(photo: PhotoMinimalEntity)

    @Query("SELECT * FROM photominimalentity")
    fun getAllPhotoMinimal(): Flow<List<PhotoMinimalEntity>>

    @Query("SELECT EXISTS(SELECT * FROM photominimalentity WHERE id = :id)")
    fun isPhotoExist(id: String): Flow<Boolean>
}