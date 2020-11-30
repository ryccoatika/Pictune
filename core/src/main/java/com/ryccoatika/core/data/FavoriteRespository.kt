package com.ryccoatika.core.data

import com.ryccoatika.core.data.source.local.LocalDataSource
import com.ryccoatika.core.domain.model.PhotoMinimal
import com.ryccoatika.core.domain.repository.IFavoriteRepository
import com.ryccoatika.core.utils.toPhotoMinimalDomain
import com.ryccoatika.core.utils.toPhotoMinimalEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class FavoriteRespository(
    private val localDataSource: LocalDataSource
) : IFavoriteRepository {
    override suspend fun addPhotoToFavorite(photo: PhotoMinimal) =
        localDataSource.insertPhotoToFavorite(photo.toPhotoMinimalEntity())

    override suspend fun deletePhotoFromFavorite(photo: PhotoMinimal) =
        localDataSource.deletePhotoFromFavorite(photo.toPhotoMinimalEntity())

    override fun getAllFavorite(): Flow<Resource<List<PhotoMinimal>>> {
        return flow {
            emit(Resource.Loading())
            val response = localDataSource.getAllPhotoFavorite().first()
            if (response.isEmpty())
                emit(Resource.Empty<List<PhotoMinimal>>())
            else
                emit(Resource.Success(response.map { it.toPhotoMinimalDomain() }))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun checkPhotoFavorited(id: String): Flow<Boolean> = localDataSource.isPhotoExistInFavorite(id)
}