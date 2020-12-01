package com.ryccoatika.pictune.user.photos

import com.ryccoatika.core.domain.model.PhotoMinimal

sealed class UserPhotosViewState {
    data class Error(val message: String?) : UserPhotosViewState()

    // discover photo
    data class Success(val data: List<PhotoMinimal>?) : UserPhotosViewState()
    object Loading : UserPhotosViewState()
    object Empty : UserPhotosViewState()

    // load more photo
    object LoadMoreLoading : UserPhotosViewState()
    object LoadMoreEmpty : UserPhotosViewState()
    data class LoadMoreSuccess(val data: List<PhotoMinimal>?) : UserPhotosViewState()
}