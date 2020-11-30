package com.ryccoatika.pictune.photo

import com.ryccoatika.core.domain.model.PhotoMinimal

sealed class PhotoViewState {
    data class Error(val message: String?) : PhotoViewState()

    // discover photo
    data class Success(val data: List<PhotoMinimal>?) : PhotoViewState()
    object Loading : PhotoViewState()
    object Empty : PhotoViewState()

    // load more photo
    object LoadMoreLoading : PhotoViewState()
    object LoadMoreEmpty : PhotoViewState()
    data class LoadMoreSuccess(val data: List<PhotoMinimal>?) : PhotoViewState()
}