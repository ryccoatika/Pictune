package com.ryccoatika.core.domain.usecase

import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.model.PhotoMinimal
import com.ryccoatika.core.domain.repository.IHistoryRepository
import kotlinx.coroutines.flow.Flow

class HistoryInteractor(
    private val historyRepository: IHistoryRepository
) : HistoryUseCase {
    override suspend fun addPhotoToHistory(photo: PhotoMinimal) = historyRepository.addPhotoToHistory(photo)

    override suspend fun deleteAllPhotoFromHistory() = historyRepository.deleteAllPhotoFromHistory()

    override fun getAllPhotoHistory(): Flow<Resource<List<PhotoMinimal>>> = historyRepository.getAllPhotoHistory()
}