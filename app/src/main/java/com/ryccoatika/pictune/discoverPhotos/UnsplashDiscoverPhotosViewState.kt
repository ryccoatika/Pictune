package com.ryccoatika.pictune.discoverPhotos

import com.ryccoatika.pictune.db.UnsplashPhotoResponse

sealed class UnsplashDiscoverPhotosViewState {
    object DiscoverLoading: UnsplashDiscoverPhotosViewState()
    object LoadMoreLoading: UnsplashDiscoverPhotosViewState()
    data class DiscoverSuccess(val responses: List<UnsplashPhotoResponse?>): UnsplashDiscoverPhotosViewState()
    data class LoadMoreSuccess(val responses: List<UnsplashPhotoResponse?>): UnsplashDiscoverPhotosViewState()
    data class DiscoverError(val error: Throwable): UnsplashDiscoverPhotosViewState()
    data class LoadMoreError(val error: Throwable): UnsplashDiscoverPhotosViewState()
}