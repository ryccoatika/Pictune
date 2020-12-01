package com.ryccoatika.pictune.user.collections

import com.ryccoatika.core.domain.model.CollectionMinimal

sealed class UserCollectionsViewState {
    data class Error(val message: String?) : UserCollectionsViewState()

    // discover photo
    object Loading : UserCollectionsViewState()
    object Empty : UserCollectionsViewState()
    data class Success(val data: List<CollectionMinimal>?) : UserCollectionsViewState()

    // load more photo
    object LoadMoreLoading : UserCollectionsViewState()
    object LoadMoreEmpty : UserCollectionsViewState()
    data class LoadMoreSuccess(val data: List<CollectionMinimal>?) : UserCollectionsViewState()
}