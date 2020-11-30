package com.ryccoatika.core.domain.repository

import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.model.PhotoMinimal
import kotlinx.coroutines.flow.Flow

interface IFavoriteRepository {
    suspend fun addPhotoToFavorite(photo: PhotoMinimal)

    suspend fun deletePhotoFromFavorite(photo: PhotoMinimal)

    fun getAllFavorite(): Flow<Resource<List<PhotoMinimal>>>

    fun checkPhotoFavorited(id: String): Flow<Boolean>
}