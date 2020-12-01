package com.ryccoatika.core.domain.usecase

import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.model.PhotoMinimal
import kotlinx.coroutines.flow.Flow

interface HistoryUseCase {
    suspend fun addPhotoToHistory(photo: PhotoMinimal)
    
    suspend fun deleteAllPhotoFromHistory()

    fun getAllPhotoHistory(): Flow<Resource<List<PhotoMinimal>>>
}