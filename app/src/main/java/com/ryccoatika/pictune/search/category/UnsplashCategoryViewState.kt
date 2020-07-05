package com.ryccoatika.pictune.search.category

import com.ryccoatika.pictune.db.UnsplashPhotoResponse
import com.ryccoatika.pictune.db.UnsplashSearchPhotosResponse

sealed class UnsplashCategoryViewState {
    object Loading : UnsplashCategoryViewState()
    object LoadMoreLoading : UnsplashCategoryViewState()
    data class Success(val response: UnsplashSearchPhotosResponse) : UnsplashCategoryViewState()
    data class LoadMoreSuccess(val response: UnsplashSearchPhotosResponse) : UnsplashCategoryViewState()
    data class Error(val error: Throwable) : UnsplashCategoryViewState()
    data class LoadMoreError(val error: Throwable) : UnsplashCategoryViewState()
}