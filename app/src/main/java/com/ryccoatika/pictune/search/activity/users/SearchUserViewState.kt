package com.ryccoatika.pictune.search.activity.users

import com.ryccoatika.core.domain.model.UserMinimal

sealed class SearchUserViewState {
    data class Error(val message: String?) : SearchUserViewState()

    // discover photo
    data class Success(val data: List<UserMinimal>?) : SearchUserViewState()
    object Loading : SearchUserViewState()
    object Empty : SearchUserViewState()

    // load more photo
    object LoadMoreLoading : SearchUserViewState()
    object LoadMoreEmpty : SearchUserViewState()
    data class LoadMoreSuccess(val data: List<UserMinimal>?) : SearchUserViewState()
}