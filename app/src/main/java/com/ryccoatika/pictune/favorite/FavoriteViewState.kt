package com.ryccoatika.pictune.favorite

import com.ryccoatika.pictune.db.UnsplashPhotoResponse
import com.ryccoatika.pictune.db.room.FavoritePhotoEntity

sealed class FavoriteViewState {
    object Loading: FavoriteViewState()
    object LoadMoreLoading: FavoriteViewState()
    data class SuccessGetFavorites(val response: List<FavoritePhotoEntity>): FavoriteViewState()
    data class SuccessLoadMore(val response: List<FavoritePhotoEntity>): FavoriteViewState()
    data class SuccessGetPhoto(val response: UnsplashPhotoResponse): FavoriteViewState()
    data class ErrorLoadMore(val error: Throwable): FavoriteViewState()
    data class Error(val error: Throwable): FavoriteViewState()
}