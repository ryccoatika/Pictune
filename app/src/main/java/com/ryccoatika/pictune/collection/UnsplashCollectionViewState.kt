package com.ryccoatika.pictune.collection

import com.ryccoatika.pictune.db.UnsplashCollectionResponse
import com.ryccoatika.pictune.db.UnsplashPhotoResponse

sealed class UnsplashCollectionViewState {
    object RelatedCollectionsLoading : UnsplashCollectionViewState()
    object PhotosLoading : UnsplashCollectionViewState()
    object LoadMorePhotosLoading : UnsplashCollectionViewState()
    data class RelatedCollectionsOnSuccess(val responses: List<UnsplashCollectionResponse?>) : UnsplashCollectionViewState()
    data class RelatedCollectionsOnError(val error: Throwable) : UnsplashCollectionViewState()
    data class PhotosOnSuccess(val responses: List<UnsplashPhotoResponse?>) : UnsplashCollectionViewState()
    data class PhotosOnError(val error: Throwable) : UnsplashCollectionViewState()
    data class LoadMorePhotosSuccess(val responses: List<UnsplashPhotoResponse?>) : UnsplashCollectionViewState()
    data class LoadMorePhotosError(val error: Throwable) : UnsplashCollectionViewState()
}