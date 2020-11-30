package com.ryccoatika.core.domain.usecase

import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.model.PhotoMinimal
import com.ryccoatika.core.domain.repository.IFavoriteRepository
import kotlinx.coroutines.flow.Flow

class FavoriteInteractor(
    private val favoriteRepository: IFavoriteRepository
) : FavoriteUseCase {
    override suspend fun addPhotoToFavorite(photo: PhotoMinimal) = favoriteRepository.addPhotoToFavorite(photo)

    override suspend fun deletePhotoFromFavorite(photo: PhotoMinimal) = favoriteRepository.deletePhotoFromFavorite(photo)

    override fun getAllFavorite(): Flow<Resource<List<PhotoMinimal>>> = favoriteRepository.getAllFavorite()

    override fun checkPhotoFavorited(id: String): Flow<Boolean> = favoriteRepository.checkPhotoFavorited(id)
}