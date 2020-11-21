package com.ryccoatika.pictune.discoverCollections

import com.ryccoatika.core.domain.model.Collection

sealed class DiscoverCollectionViewState {
    data class Error(val message: String?) : DiscoverCollectionViewState()

    // discover photo
    object DiscoverLoading : DiscoverCollectionViewState()
    object DiscoverEmpty : DiscoverCollectionViewState()
    data class DiscoverSuccess(val data: List<Collection>?) : DiscoverCollectionViewState()

    // load more photo
    object LoadMoreLoading : DiscoverCollectionViewState()
    object LoadMoreEmpty : DiscoverCollectionViewState()
    data class LoadMoreSuccess(val data: List<Collection>?) : DiscoverCollectionViewState()
}