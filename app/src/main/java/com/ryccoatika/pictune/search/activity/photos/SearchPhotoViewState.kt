package com.ryccoatika.pictune.search.activity.photos

import com.ryccoatika.core.domain.model.PhotoMinimal

sealed class SearchPhotoViewState {
    data class Error(val message: String?) : SearchPhotoViewState()

    // discover photo
    data class Success(val data: List<PhotoMinimal>?) : SearchPhotoViewState()
    object Loading : SearchPhotoViewState()
    object Empty : SearchPhotoViewState()

    // load more photo
    object LoadMoreLoading : SearchPhotoViewState()
    object LoadMoreEmpty : SearchPhotoViewState()
    data class LoadMoreSuccess(val data: List<PhotoMinimal>?) : SearchPhotoViewState()
}