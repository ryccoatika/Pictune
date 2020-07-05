package com.ryccoatika.pictune.user.inventory

import com.ryccoatika.pictune.db.UnsplashCollectionResponse
import com.ryccoatika.pictune.db.UnsplashPhotoResponse

sealed class UnsplashUserInventoryViewState {
    object Loading: UnsplashUserInventoryViewState()
    object LoadMoreLoading: UnsplashUserInventoryViewState()
    data class SuccessPhotos(val responses: List<UnsplashPhotoResponse?>): UnsplashUserInventoryViewState()
    data class LoadMoreSuccessPhotos(val responses: List<UnsplashPhotoResponse?>): UnsplashUserInventoryViewState()
    data class SuccessLikes(val responses: List<UnsplashPhotoResponse?>): UnsplashUserInventoryViewState()
    data class LoadMoreSuccessLikes(val responses: List<UnsplashPhotoResponse?>): UnsplashUserInventoryViewState()
    data class SuccessCollections(val responses: List<UnsplashCollectionResponse?>): UnsplashUserInventoryViewState()
    data class LoadMoreSuccessCollections(val responses: List<UnsplashCollectionResponse?>): UnsplashUserInventoryViewState()
    data class Error(val error: Throwable): UnsplashUserInventoryViewState()
    data class LoadMoreError(val error: Throwable): UnsplashUserInventoryViewState()
}