package com.ryccoatika.pictune.photo

import com.ryccoatika.pictune.db.UnsplashPhotoStatisticsResponse

sealed class UnsplashPhotoViewState {
    data class LoadStats(val response: UnsplashPhotoStatisticsResponse): UnsplashPhotoViewState()
    data class Error(val error: Throwable): UnsplashPhotoViewState()
}