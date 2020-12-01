package com.ryccoatika.core.data.source.local

import com.ryccoatika.core.data.source.local.entity.PhotoHistoryEntity
import com.ryccoatika.core.data.source.local.entity.PhotoMinimalEntity
import com.ryccoatika.core.data.source.local.room.FavoriteDao
import com.ryccoatika.core.data.source.local.room.HistoryDao
import kotlinx.coroutines.flow.Flow

class LocalDataSource(
    private val favoriteDao: FavoriteDao,
    private val historyDao: HistoryDao
) {
    suspend fun insertPhotoToFavorite(photo: PhotoMinimalEntity) = favoriteDao.insertPhotoMinimal(photo)

    suspend fun deletePhotoFromFavorite(photo: PhotoMinimalEntity) = favoriteDao.deletePhotoMinimal(photo)

    fun getAllPhotoFavorite(): Flow<List<PhotoMinimalEntity>> = favoriteDao.getAllPhotoMinimal()

    fun isPhotoExistInFavorite(id: String): Flow<Boolean> = favoriteDao.isPhotoExist(id)

    suspend fun insertPhotoToHistory(photoHistoryEntity: PhotoHistoryEntity) =
        historyDao.insertPhoto(photoHistoryEntity)

    suspend fun deleteAllPhotoFromHistory() = historyDao.deleteAllPhoto()

    fun getAllPhotoHistory(): Flow<List<PhotoHistoryEntity>> =
        historyDao.getAllPhoto()
}