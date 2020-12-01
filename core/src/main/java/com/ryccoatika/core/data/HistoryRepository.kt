package com.ryccoatika.core.data

import com.ryccoatika.core.data.source.local.LocalDataSource
import com.ryccoatika.core.domain.model.PhotoMinimal
import com.ryccoatika.core.domain.repository.IHistoryRepository
import com.ryccoatika.core.utils.toPhotoHistoryEntity
import com.ryccoatika.core.utils.toPhotoMinimalDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class HistoryRepository(
    private val localDataSource: LocalDataSource
): IHistoryRepository {
    override suspend fun addPhotoToHistory(photo: PhotoMinimal) =
        localDataSource.insertPhotoToHistory(photo.toPhotoHistoryEntity())

    override suspend fun deleteAllPhotoFromHistory() =
        localDataSource.deleteAllPhotoFromHistory()

    override fun getAllPhotoHistory(): Flow<Resource<List<PhotoMinimal>>> {
        return flow {
            emit(Resource.Loading())
            val response = localDataSource.getAllPhotoHistory().first()
            if (response.isEmpty())
                emit(Resource.Empty<List<PhotoMinimal>>())
            else
                emit(Resource.Success(response.map { it.toPhotoMinimalDomain() }))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

}