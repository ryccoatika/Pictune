package com.ryccoatika.pictune.collection

import com.ryccoatika.core.domain.model.CollectionMinimal

sealed class CollectionViewState {
    data class Error(val message: String?) : CollectionViewState()

    // discover photo
    object Loading : CollectionViewState()
    object Empty : CollectionViewState()
    data class Success(val data: List<CollectionMinimal>?) : CollectionViewState()

    // load more photo
    object LoadMoreLoading : CollectionViewState()
    object LoadMoreEmpty : CollectionViewState()
    data class LoadMoreSuccess(val data: List<CollectionMinimal>?) : CollectionViewState()
}