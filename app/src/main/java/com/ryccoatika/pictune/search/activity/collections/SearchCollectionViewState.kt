package com.ryccoatika.pictune.search.activity.collections

import com.ryccoatika.core.domain.model.CollectionMinimal

sealed class SearchCollectionViewState {
    data class Error(val message: String?) : SearchCollectionViewState()

    // discover photo
    object Loading : SearchCollectionViewState()
    object Empty : SearchCollectionViewState()
    data class Success(val data: List<CollectionMinimal>?) : SearchCollectionViewState()

    // load more photo
    object LoadMoreLoading : SearchCollectionViewState()
    object LoadMoreEmpty : SearchCollectionViewState()
    data class LoadMoreSuccess(val data: List<CollectionMinimal>?) : SearchCollectionViewState()
}