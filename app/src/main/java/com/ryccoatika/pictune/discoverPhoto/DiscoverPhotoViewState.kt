package com.ryccoatika.pictune.discoverPhoto

import com.ryccoatika.core.domain.model.PhotoMinimal

sealed class DiscoverPhotoViewState {
    data class Error(val message: String?): DiscoverPhotoViewState()

    // discover photo
    data class DiscoverSuccess(val data: List<PhotoMinimal>?): DiscoverPhotoViewState()
    object DiscoverLoading: DiscoverPhotoViewState()
    object DiscoverEmpty: DiscoverPhotoViewState()

    // load more photo
    object LoadMoreLoading: DiscoverPhotoViewState()
    object LoadMoreEmpty: DiscoverPhotoViewState()
    data class LoadMoreSuccess(val data: List<PhotoMinimal>?): DiscoverPhotoViewState()
}