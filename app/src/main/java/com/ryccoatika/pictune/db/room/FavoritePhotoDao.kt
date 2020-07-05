package com.ryccoatika.pictune.db.room

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface FavoritePhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: FavoritePhotoEntity): Completable
    @Delete
    fun delete(data: FavoritePhotoEntity): Completable
    @Query("SELECT * FROM ${FavoritePhotoEntity.TABLE_NAME} WHERE ${FavoritePhotoEntity.COLUMN_ID} = :id")
    fun getPhotoById(id: String): Single<List<FavoritePhotoEntity>>
    @Query("SELECT * FROM ${FavoritePhotoEntity.TABLE_NAME}")
    fun getAllData(): Single<List<FavoritePhotoEntity>>
}