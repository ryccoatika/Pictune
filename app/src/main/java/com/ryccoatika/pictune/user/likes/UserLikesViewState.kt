package com.ryccoatika.pictune.user.likes

import com.ryccoatika.core.domain.model.PhotoMinimal

sealed class UserLikesViewState {
    data class Error(val message: String?) : UserLikesViewState()

    // discover photo
    data class Success(val data: List<PhotoMinimal>?) : UserLikesViewState()
    object Loading : UserLikesViewState()
    object Empty : UserLikesViewState()

    // load more photo
    object LoadMoreLoading : UserLikesViewState()
    object LoadMoreEmpty : UserLikesViewState()
    data class LoadMoreSuccess(val data: List<PhotoMinimal>?) : UserLikesViewState()
}